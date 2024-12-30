package com.pelatro.automation.validationOfLogin.utils;



import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
 
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
 
public class Assertion {
	
	public List<String> filedMessages = new ArrayList<String>();;
	private boolean isDebudModeOnForAssertions;
 
	public void assertThat(String reason, boolean assertion) {
		if (!assertion) {
			for (int i = 1 ; i <= 10 ; i++) 
				Toolkit.getDefaultToolkit().beep();
			throw new AssertionError(reason);
		} else if (isDebudModeOnForAssertions)
			System.out.println("\n<<<<< VALIDATION PASSED AGAINST >>>>> " + reason + "\n");
	}
	public <T> void assertThat(T actual, Matcher<? super T> matcher) {
		assertThat("", actual, matcher);
	}
 
	public <T> void assertThat(String reason, T actual, Matcher<? super T> matcher) {
		if (!matcher.matches(actual)) {
			Description description = new StringDescription();
			description.appendText(reason).appendText("\nExpected: ").appendDescriptionOf(matcher)
					.appendText("\n     But: it ");
			matcher.describeMismatch(actual, description);
			throw new AssertionError(description.toString());
		} else if (isDebudModeOnForAssertions)
			System.out.println("\n<<<<< VALIDATION PASSED AGAINST >>>>> " + reason + "\n");
	}
	public <T> void fileIt(String message, T actual, Matcher<? super T> matcher) {
		if (!matcher.matches(actual)) {
			Description description = new StringDescription();
			description.appendText(message).appendText("\nExpected: ").appendDescriptionOf(matcher)
					.appendText("\n     But: it ");
			matcher.describeMismatch(actual, description);
			System.out.println("%%%%%%% Filing the issue >>> " + message);
			filedMessages.add(description.toString());
		} else if (isDebudModeOnForAssertions)
			System.out.println("\n<<<<< VALIDATION PASSED AGAINST >>>>> " + message + "\n");
	}
	public void fileIt(String message) {
		filedMessages.add(message);
	}
	public void fireIt() {
		if (filedMessages.isEmpty())
			return;
		else {
			List<String> issues = new ArrayList<>();
			issues.addAll(filedMessages);
			filedMessages.clear();
			assertThat(String.valueOf(issues), Boolean.FALSE);
		}
	}
	public void clearFiledIssues() {
		filedMessages.clear();
	}
	public void fileIt(String message, boolean assertion) {
		if (!assertion) {
			Description description = new StringDescription();
			description.appendText(message);
			filedMessages.add(description.toString());
		} else if (isDebudModeOnForAssertions)
			System.out.println("\n<<<<< VALIDATION PASSED AGAINST >>>>> " + message + "\n");
	}
}
