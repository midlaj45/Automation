package com.pelatro.automation.validationOfLogin.steps;

import com.pelatro.automation.validationOfLogin.login.LoginPage;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class LoginSteps {
	
	private LoginPage loginPage;
	
	@Given("I opened the app using following (.*)")
	public void openApp(String url) {
		loginPage.openAt(url);
		
	}
	
	@When("I type (.*) in the username field")
	public void getUsername(String username) {
		loginPage.type("//input[@id='username']", username);
		
	}
	
	@And("I type (.*) in the password field")
	public void getPassword(String password) {
		loginPage.type("//input[@id='password']",password);
		
	}
	
	@And("I press the submit buttom")
	public void clickSubmit() {
		loginPage.click("//button[@id='submit']");
		
	}
	
	@Then("I redirected to new page having url (,*)")
	public void redirectToNewPage(String url) {
		assert loginPage.verifyUrl(url);
		
	}
	
	@And("I able to see the (.*) and (.*)")
	public void searchForText(String message,String text) {
		assert loginPage.searchText(message);
		assert loginPage.searchText(text);
	}
	
	@And("I able to see the log out button")
	public void verifyLogoutButton() {
		assert loginPage.verify("//a[@class='wp-block-button__link has-text-color has-background has-very-dark-gray-background-color']");
		
	}
	
	
	
	@Then("I see an error message (.*)")
	public void verifyErrorText(String message) {
		assert loginPage.searchText(message);
		
	}

}