
Feature: Login

  Scenario Outline: positive login
    Given I opened the app using following <URL>
    When I type <username> in the username field
    And I type <password> in the password field
    And I press the submit buttom
    Then I redirected to new page having url <url>
    And I able to see the <message> and <text>
    And I able to see the log out button

    Examples: 
      | URL                                                     |username|password   | message       |text                  |url|
      | https://practicetestautomation.com/practice-test-login/ |student |Password123|Congratulations|successfully logged in|https://practicetestautomation.com/logged-in-successfully/|
      
      
   Scenario Outline: Negative username test
    Given I opened the app using following <URL>
    When I type <username> in the username field
    And I type <password> in the password field
    And I press the submit buttom
    Then I see an error message <errormsg>
    
     Examples:
         | URL                                                     |username|password   | errormsg                 |
         | https://practicetestautomation.com/practice-test-login/ |stud    |Password123|Your username is invalid! |
      
    Scenario Outline: Negative password test
    Given I opened the app using following <URL>
    When I type <username> in the username field
    And I type <password> in the password field
    And I press the submit buttom
    Then I see an error message <errormsg>
    
     Examples:
         | URL                                                     |username   |password   | errormsg                 |
         | https://practicetestautomation.com/practice-test-login/ |student    |Password3  |Your password is invalid! |
          
  
   
   
    
    