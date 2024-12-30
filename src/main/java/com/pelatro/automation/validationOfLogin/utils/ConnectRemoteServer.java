package com.pelatro.automation.validationOfLogin.utils;



import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
 
public class ConnectRemoteServer {
 
	private static Logger logger = LoggerFactory.getLogger(ConnectRemoteServer.class);
	private RemoteServerProperties remoteServerProperties;
	private Assertion assertion;
	private Session session;
	private Channel channel;
	public ConnectRemoteServer() {
		assertion = new Assertion();
		remoteServerProperties = new RemoteServerProperties();
		setter();
	}
 
	private String bashrc;
 
	private String userName;
	private String password;
	private String hostIP;
 
	private boolean shouldShowCommand;
 
	private String cmd;
	private String space;
	private int port;
 
	private String machine53Error1;
	private long commandExecutionTimoutInMillisecs;
 
	private void setter() {
		bashrc = remoteServerProperties.getBashrcFilePath() + "/conf/shell_script/path.properties";
		userName = remoteServerProperties.getUserName();
		password = remoteServerProperties.getPassword();
		hostIP = remoteServerProperties.getIp();
		shouldShowCommand = remoteServerProperties.isShouldShowCommandsInConsoleLog();
		cmd = remoteServerProperties.getAtScriptDir();
		port = remoteServerProperties.getPort();
		space = " ";
		machine53Error1 = "df: ‘/run/user/1000/gvfs’: Transport endpoint is not connected";
		commandExecutionTimoutInMillisecs = remoteServerProperties.getCommandExecutionTimoutInMillisecs();
	}
 
	/**
	 * Start the session with the host.
	 * 
	 * @return true if the session started successfully, false otherwise
	 */
	public boolean startSession() {
		// log.info("Establishing new remote connection for server side execution !!!");
		JSch jsch = new JSch();
		try {
			session = jsch.getSession(userName, hostIP, port);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
		} catch (JSchException jsche) {
			System.out.println(jsche.getMessage());
			return false;
		}
		return true;
	}
 
	/**
	 * Execute commands on the host;
	 * 
	 * @param shFileOrCommand
	 *            command to be executed on the host.
	 * @return status of the execution
	 */
	public String execute(String shFileOrCommand, String... arg) {
 
		String success = "";
 
		/*
		 * if (session != null && session.isConnected())
		 * log.info("Using previous remote connection !!!"); else startSession();
		 */
		startSession();
 
		assertion.assertThat("Failed to connect with remote server !!!", session.isConnected());
 
		shFileOrCommand = shFileOrCommand.endsWith(".sh")
				? (cmd + (!shFileOrCommand.trim().startsWith("/") ? "/" + shFileOrCommand.trim()
						: shFileOrCommand.trim()) + space + bashrc + space + String.join(space, arg))
				: shFileOrCommand.trim();
 
		shFileOrCommand = shFileOrCommand.replace("\n", " ");
 
		if (Boolean.valueOf(shouldShowCommand) /* && !shFileOrCommand.contains("read_product_configs") */)
			logger.info("\nCommand to be executed :: \n" + shFileOrCommand + "\n");
 
		if (session != null && session.isConnected()) {
			try {
				channel = session.openChannel("exec");
				((ChannelExec) channel).setPty(true); // Need to set as false to exit from terminal
				((ChannelExec) channel).setCommand(shFileOrCommand);
 
				InputStream in = channel.getInputStream();
 
				channel.connect();
			    long startTime = System.currentTimeMillis();
				byte[] buffer = new byte[1024];
				while (!channel.isClosed()) {
					if (isExecutionTimedOut(startTime)) {
						logger.info("Execution timeout, exiting loop...");
		                break;
		            }
					while (in.available() > 0) {
						success = readInput(success, in, buffer);
						if (isExecutionTimedOut(startTime)) {
							logger.info("Execution timeout, exiting loop...");
			                break;
			            }
					}
				}
				while (in.available() > 0) {
					success = readInput(success, in, buffer);
					if (isExecutionTimedOut(startTime)) {
						logger.info("Execution timeout, exiting loop...");
		                break;
		            }
				}
			} catch (JSchException jsche) {
				System.out.println(jsche.getMessage());
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
			} finally {
				if (channel != null && channel.isConnected())
					channel.disconnect();
				stopSession();
			}
		}
		success = success.replace(machine53Error1, "").trim();
		return success;
	}
 
	private String readInput(String success, InputStream in, byte[] buffer) throws IOException {
		int i = in.read(buffer, 0, 1024);
		success += new String(buffer, 0, i);
		return success;
	}
 
	/**
	 * Stop the session with the remote.
	 * 
	 */
	public void stopSession() {
		if (session != null && session.isConnected())
			session.disconnect();
	}
 
	public String executeShell(String shFileOrCommand, String... arg) {
		String success = "";
		try {
			shFileOrCommand = shFileOrCommand.endsWith(".sh")
					? (cmd + (!shFileOrCommand.trim().startsWith("/") ? "/" + shFileOrCommand.trim()
							: shFileOrCommand.trim()) + space + bashrc + space + String.join(space, arg))
					: shFileOrCommand.trim();
			shFileOrCommand = shFileOrCommand.replace("\n", " ");
			shFileOrCommand = shFileOrCommand + "\nexit\n";
			if (Boolean.valueOf(shouldShowCommand) && !shFileOrCommand.contains("read_product_configs"))
				System.out.println("\nCommand to be executed :: \n" + shFileOrCommand + "\n");
			JSch jsch = new JSch();
			Session session = jsch.getSession(userName, hostIP, port);
			session.setUserInfo(new sshRemoteExampleUserInfo(userName, password));
			session.connect();
			Channel channel = session.openChannel("shell");
			channel.setInputStream(new ByteArrayInputStream(shFileOrCommand.getBytes(StandardCharsets.UTF_8)));
			channel.setOutputStream(System.out);
			InputStream in = channel.getInputStream();
			StringBuilder outBuff = new StringBuilder();
			int exitStatus = -1;
			channel.connect();
		    long startTime = System.currentTimeMillis();
			while (true) {
				for (int c; ((c = in.read()) >= 0);) {
					outBuff.append((char) c);
				}
				if (channel.isClosed()) {
					if (in.available() > 0)
						continue;
					exitStatus = channel.getExitStatus();
					break;
				}
				if (isExecutionTimedOut(startTime)) {
					logger.info("Execution timeout, exiting loop...");
	                break;
	            }
			}
			channel.disconnect();
			session.disconnect();
			// print the buffer's contents
			success = outBuff.toString();
			// print exit status
			System.out.print("Exit status of the execution: " + exitStatus);
			if (exitStatus == 0) {
				System.out.print(" (OK)\n");
			} else {
				System.out.print(" (NOK)\n");
			}
		} catch (IOException | JSchException ioEx) {
			System.err.println(ioEx.toString());
		}
		success = success.replace(machine53Error1, "").trim();
		return success;
	}
 
	public void send(String dir, String localFile, String fileName) {
		startSession();
		Channel channel = null;
		ChannelSftp channelSftp = null;
		logger.debug("preparing the host information for sftp.");
		try {
			logger.debug("Host connected.");
			channel = session.openChannel("sftp");
			channel.connect();
			logger.debug("sftp channel opened and connected.");
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(dir);
			File f = new File(fileName);
			channelSftp.put(new FileInputStream(new File(localFile)), f.getName());
			logger.info("File transfered successfully to host -> " + hostIP);
		} catch (Exception ex) {
			logger.error("Exception found while tranfer the response.", ex);
		} finally {
			channelSftp.exit();
			logger.debug("sftp Channel exited.");
			channel.disconnect();
			logger.debug("Channel disconnected.");
			session.disconnect();
			logger.debug("Host Session disconnected.");
		}
	}
 
	public String download(String destFolder, String filePath) {
		String fileName = new File(filePath).getName();
		if (!new File(destFolder).exists())
			new File(destFolder).mkdirs();
		startSession();
		Channel channel = null;
		ChannelSftp channelSftp = null;
		logger.debug("preparing the host information for sftp.");
		try {
			logger.debug("Host connected.");
			channel = session.openChannel("sftp");
			channel.connect();
			logger.debug("sftp channel opened and connected.");
			channelSftp = (ChannelSftp) channel;
			channelSftp.get(filePath, destFolder + "/" + fileName);
			logger.info("File downloaded successfully to local...");
			return (destFolder + "/" + fileName);
		} catch (Exception ex) {
			logger.error("Exception found while tranfer the response.", ex);
		} finally {
			channelSftp.exit();
			logger.debug("sftp Channel exited.");
			channel.disconnect();
			logger.debug("Channel disconnected.");
			session.disconnect();
			logger.debug("Host Session disconnected.");
		}
		return null;
	}
	public String download(String destFolder, String destFileName, String srcFilePath) {
		if (!new File(destFolder).exists())
			new File(destFolder).mkdirs();
		startSession();
		Channel channel = null;
		ChannelSftp channelSftp = null;
		logger.debug("preparing the host information for sftp.");
		try {
			logger.debug("Host connected.");
			channel = session.openChannel("sftp");
			channel.connect();
			logger.debug("sftp channel opened and connected.");
			channelSftp = (ChannelSftp) channel;
			channelSftp.get(srcFilePath, destFolder + "/" + destFileName);
			logger.info("File downloaded successfully to local...");
			return (destFolder + "/" + destFileName);
		} catch (Exception ex) {
			logger.error("Exception found while tranfer the response.", ex);
		} finally {
			channelSftp.exit();
			logger.debug("sftp Channel exited.");
			channel.disconnect();
			logger.debug("Channel disconnected.");
			session.disconnect();
			logger.debug("Host Session disconnected.");
		}
		return null;
	}
	public String getRemoteMachineUserName() {
		return userName;
	}
	private boolean isExecutionTimedOut(long startTime) {
		return System.currentTimeMillis() - startTime > commandExecutionTimoutInMillisecs;
	}
}
