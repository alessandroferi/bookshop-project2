Feature: Registration and Login processes
		Specifications of authentication process

	Scenario: Registration process
  	Given I start on the Registration page
  	When I insert "my_email@gmail" into email field, "my_username" into username field and "my_password" into password field
  	And I click the "Register" button
  	Then I am on the "Result" page 
  	And "You have successfully registered!" message is shown
  	
  Scenario: Failed registration process due to an already existing email address
    Given I start on the Registration page
  	When I insert "my_email@gmail" into email field, "my_username" into username field and "my_password" into password field
  	And I click the "Register" button
  	Then I am on the "Result" page
  	And "You have successfully registered!" message is shown
  	Given I start on the Registration page
  	When I insert "my_email@gmail" into email field, "another_username" into username field and "another_password" into password field
  	And I click the "Register" button
  	Then I am on the "Result" page 
  	And "There is already a user registered with the email provided. Please, try with another email address." message is shown
  	
  Scenario: Failed registration process due to an already existing username  
  	Given I start on the Registration page
  	When I insert "my_email@gmail" into email field, "my_username" into username field and "my_password" into password field
  	And I click the "Register" button
  	Then I am on the "Result" page
  	And "You have successfully registered!" message is shown
  	Given I start on the Registration page
  	When I insert "another_email@gmail" into email field, "my_username" into username field and "another_password" into password field
  	And I click the "Register" button
  	Then I am on the "Result" page 
  	And "There is already a user registered with the username provided. Please, try with another username." message is shown
  	
  Scenario: Failed Login process
		Given I start on the Registration page
  	When I insert "my_email@gmail" into email field, "my_username" into username field and "my_password" into password field
  	And I click the "Register" button
  	Then I am on the "Result" page
  	And "You have successfully registered!" message is shown
  	When I click on "Login Page" link
  	And I insert "wrong_username" into username field and "wrong_password" into password field
  	And I click the "Sign in" button
  	Then I am on the "Login" page 
  	And "Invalid username and password." message is shown
  	
  Scenario: Successful Login and Logout
  	Given I start on the Registration page
  	When I insert "my_email@gmail" into email field, "my_username" into username field and "my_password" into password field
  	And I click the "Register" button
  	Then I am on the "Result" page 
  	And "You have successfully registered!" message is shown
  	When I click on "Login Page" link
  	And I insert "my_username" into username field and "my_password" into password field
  	And I click the "Sign in" button
  	Then I am on the "Home" page
  	When I click the "Logout" button
  	Then I am on the "Login" page
  	
  	
  	
  
  
  	