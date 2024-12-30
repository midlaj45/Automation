package com.pelatro.automation.validationOfLogin.utils;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class RemoteServerProperties {
	private static final Logger logger = LoggerFactory.getLogger(RemoteServerProperties.class);
	private String remoteServerDetailsFilePath;
	private Properties serverDetailsProperties;
	private Map<String, String> remoteServerConfig;
	public RemoteServerProperties() {
		remoteServerConfig = new HashMap<String, String>();
		remoteServerDetailsFilePath = System.getProperty("user.dir") + "/machine_details.properties";
		readProperties(new File(remoteServerDetailsFilePath));
		setter();
	}
 
	private Map<String, String> readProperties(File fileNameAndPath) {
		InputStream input = null;
		try {
			input = new FileInputStream(fileNameAndPath);
			serverDetailsProperties = new Properties();
			serverDetailsProperties.load(input);
			Set<String> propertyNames = serverDetailsProperties.stringPropertyNames();
			for (String Property : propertyNames) {
				remoteServerConfig.put(Property, serverDetailsProperties.getProperty(Property));
			}
		} catch (FileNotFoundException FNFE) {
			logger.error(FNFE.getMessage());
		} catch (IOException IOE) {
			logger.error(IOE.getMessage());
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return remoteServerConfig;
	}
 
	private String bashrcFilePath;
	private String userName;
 
	private String password;
 
	private int port;
 
	private String ip;
 
	private String atScriptDir;
 
	private boolean shouldShowCommandsInConsoleLog;
	private long commandExecutionTimoutInMillisecs;
 
	public String getBashrcFilePath() {
		return bashrcFilePath;
	}
 
	public void setBashrcFilePath() {
		String[] path = System.getProperty("user.dir").split("/");
		path[2] = getUserName();
		this.bashrcFilePath = String.join("/", path);
	}
 
	public String getUserName() {
		return userName;
	}
 
	public void setUserName(String userName) {
		this.userName = userName;
	}
 
	public String getPassword() {
		return password;
	}
 
	public void setPassword(String password) {
		this.password = password;
	}
 
	public int getPort() {
		return port;
	}
 
	public void setPort(int port) {
		this.port = port;
	}
 
	public String getIp() {
		return ip;
	}
 
	public void setIp(String ip) {
		this.ip = ip;
	}
 
	public String getAtScriptDir() {
		return atScriptDir;
	}
 
	public void setAtScriptDir() {
		String[] path = System.getProperty("user.dir").split("/");
		path[2] = getUserName();
		String shellFolder = "";
		for (int i = 0; i < path.length - 1; i++) {
			if (!path[i].equals(""))
				shellFolder += "/" + path[i];
		}
		this.atScriptDir = shellFolder;
	}
 
	public boolean isShouldShowCommandsInConsoleLog() {
		return shouldShowCommandsInConsoleLog;
	}
 
	public void setShouldShowCommandsInConsoleLog(boolean shouldShowCommandsInConsoleLog) {
		this.shouldShowCommandsInConsoleLog = shouldShowCommandsInConsoleLog;
	}
	private void setter() {
		setUserName(remoteServerConfig.get("username"));
		setPassword(remoteServerConfig.get("password"));
		setPort(Integer.parseInt(remoteServerConfig.get("port")));
		setIp(remoteServerConfig.get("ip"));
		setShouldShowCommandsInConsoleLog(Boolean.valueOf(remoteServerConfig.get("should_show_commands_in_console_log")));
		setCommandExecutionTimoutInMillisecs(Long.valueOf(remoteServerConfig.get("command_execution_timeout_in_millisecs")));
		setBashrcFilePath();
		setAtScriptDir();
	}
 
	public long getCommandExecutionTimoutInMillisecs() {
		return commandExecutionTimoutInMillisecs;
	}
 
	public void setCommandExecutionTimoutInMillisecs(long commandExecutionTimoutInMillisecs) {
		this.commandExecutionTimoutInMillisecs = commandExecutionTimoutInMillisecs;
	}
 
}