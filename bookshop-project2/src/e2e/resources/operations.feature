Feature: Operations inside the bookshop site
		Specifications of database operations
		
	Background: 
		Given I am on the Registration page
  	When I insert "my_email@gmail" into email field, "my_username" into username field and "my_password" into password field
  	And I click the "Register" button
  	Then I am on the "Result" page
  	And "You have successfully registered!" message is shown
  	When I click on "Login Page" link
  	And I insert "my_email@gmail" into email field and "my_password" into password field
  	And I click the "Sign in" button
  	Then I am on the "Home" page
		
	
	Scenario: Initially empty book list in the Home Page
  	And "There are no books" message is shown
  	
  Scenario: Add a book and delete it
		When I click the "Insert" button
		And I insert "TestTitleToDelete" in title field, "TestAuthorToDelete" in author field and "30" in price field 
		And I click the "Save" button
		Then The "Book Table" is shown and it contains a book with "TestTitleToDelete", "TestAuthorToDelete", and price "30"
		When I click the "Delete" button
		Then "There are no books" message is shown
	
	Scenario: Add a book and edit it
		When I click the "Insert" button
		And I insert "TestTitleToEdit" in title field, "TestAuthorToEdit" in author field and "30" in price field 
		And I click the "Save" button
		Then The "Book Table" is shown and it contains a book with "TestTitleToEdit", "TestAuthorToEdit", and price "30"
		When I click the "Edit" button
		And I update the author field with "UpdatedAuthor" and the price field with "20"
		And I click the "Save" button
		Then The "Book Table" is shown and it contains a book with "TestTitleToEdit", "UpdatedAuthor", and price "20"
		
	Scenario: Add a new Book and search it
		When I click the "Insert" button
		And I insert "TestTitleToSearch" in title field, "TestAuthorToSearch" in author field and "10" in price field 
		And I click the "Save" button
		Then The "Book Table" is shown and it contains a book with "TestTitleToSearch", "TestAuthorToSearch", and price "10"
  	When I insert "TestTitleToSearch" in the search field
		And I click the "Search" button
		Then I am on the "Search" page
		Then The "Result Table" is shown and it contains a book with "TestTitleToSearch", "TestAuthorToSearch", and price "10"
		When I click on "Home" link
		Then I am on the "Home" page
		
	Scenario: Failing book research due to empty search field
  	When I click the "Insert" button
  	And I insert "TestTitle" in title field, "TestAuthor" in author field and "25" in price field 
		And I click the "Save" button
		Then The "Book Table" is shown and it contains a book with "TestTitle", "TestAuthor", and price "25"
  	When I insert "" in the search field
		And I click the "Search" button
		Then I am on the "Search" page
		And "Error! Please, insert a valid title." message is shown
		
	Scenario: Failing book research due to book not found
  	When I click the "Insert" button
  	And I insert "Title" in title field, "Author" in author field and "25" in price field 
		And I click the "Save" button
		Then The "Book Table" is shown and it contains a book with "Title", "Author", and price "25"
  	When I insert "Title Not Found" in the search field
		And I click the "Search" button
		Then I am on the "Book not found" page
		And "Book not found!" message is shown
		
	Scenario: Delete All books from the table
		When I click the "Insert" button
		And I insert "TestTitleForDeleteAll" in title field, "TestAuthorForDeleteAll" in author field and "50" in price field 
		And I click the "Save" button
		Then The "Book Table" is shown and it contains a book with "TestTitleForDeleteAll", "TestAuthorForDeleteAll", and price "50"
		When I click the "Delete All" button
		Then "There are no books" message is shown