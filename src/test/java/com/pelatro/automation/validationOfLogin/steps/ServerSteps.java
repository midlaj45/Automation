package com.pelatro.automation.validationOfLogin.steps;



import com.pelatro.automation.validationOfLogin.utils.ConnectRemoteServer;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ServerSteps {
	
	@Given("I created a directory (.*) in my home directory")
	public void createDirectory(String dirName) {
		new ConnectRemoteServer().execute("mkdir -p /home/pelatro/" + dirName);
		String ls = new ConnectRemoteServer().execute("ls /home/pelatro");
	        if (!ls.contains(dirName))
			throw new AssertionError("Directory creation is failed !!!");
	}
	
	
	@When("I write 5 files with 10 lines each in (.*)")
	public void writeIntoFiles(String dirName) {
	    for (int i = 1; i <= 5; i++) {
	        String filePath = "/home/pelatro/" + dirName + "/f" + i + ".txt";     
	        new ConnectRemoteServer().execute("touch " + filePath); 
	        String lsOutput = new ConnectRemoteServer().execute("ls /home/pelatro/" + dirName);
	        if (!lsOutput.contains("f" + i + ".txt")) {
	            throw new AssertionError("Failed to create file");
	        }
	        for (int j = 1; j <= 10; j++) {
	            new ConnectRemoteServer().execute("echo 'Line " + j +"' >> " + filePath);
	            
	        }

            String fileContent = new ConnectRemoteServer().execute("cat " + filePath);
            if (fileContent == null || fileContent.split("\n").length != 10) {
                throw new AssertionError("File " + filePath + " does not contain 10 lines as expected.");
            }
	    }
	}

	@Then("I list all the files in (.*)")
	public void listingFiles(String dirName) {
	    String lsOutput = new ConnectRemoteServer().execute("ls /home/pelatro/" + dirName);
	    if (lsOutput == null || lsOutput.trim().isEmpty()) {
	        throw new AssertionError("No files found in directory: " + dirName);
	    }
	    System.out.println("Files in directory " + dirName + ":");
	    System.out.println(lsOutput);
	    
	   
	}
	@Then("I concatenate all files in (.*) into a temp file")
	public void concatenateFilesToTemp(String dirName) {
	    ConnectRemoteServer remoteServer = new ConnectRemoteServer();

	      String directoryPath = "/home/pelatro/" + dirName;
	    String tempFilePath = directoryPath + "/temp.txt";

	  
	    remoteServer.execute("cd " + directoryPath + " && touch temp.txt");

	    String lsOutput = remoteServer.execute("ls " + directoryPath);
	    if (!lsOutput.contains("temp.txt")) {
	        throw new AssertionError("Failed to create temp file in " + dirName + "!");
	    }

	    remoteServer.execute("cd " + directoryPath + " && for file in *; do cat \"$file\" >> temp.txt; done");

	    String tempFileContent = remoteServer.execute("cat " + tempFilePath);
	    if (tempFileContent.isEmpty()) {
	        throw new AssertionError("Temp file is empty after concatenation");
	    }
	 
	    System.out.println("All files concatenated into " + tempFilePath);
	  
	}
	@Then("I download the (.*) from (.*)")
	public void i_download_the_temp_file(String fileName,String dirName) {
		new ConnectRemoteServer().download("/home/pelatro/Downloads","/home/pelatro/"+dirName+"/"+fileName);
		if(!new ConnectRemoteServer().execute("ls /home/pelatro/Downloads/").contains(fileName))
			throw new AssertionError("Downloaded file not present");
	}

	@Then("i print the content in the (.*)")
	public void iPrintTheContent(String fileName) {
		String fileContent =new ConnectRemoteServer().execute("cat /home/pelatro/Downloads/"+fileName);
		if (fileContent == null ) {
	        throw new AssertionError("The file " + fileName + "is empty");
	    }
		System.out.println(fileContent);
 

	}

}

