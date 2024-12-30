package com.pelatro.automation.validationOfLogin;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources", plugin = {
		"rerun:target/rerun.txt" }, glue = {
				"com.pelatro.automation.validationOfLogin.steps" }, tags = { "" }, format = { "pretty" })

public class ClientRunnerClass {

}
