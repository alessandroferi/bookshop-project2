package com.feri.alessandro.attsw.project.e2e.steps;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.feri.alessandro.attsw.project.repositories.BookRepository;
import com.feri.alessandro.attsw.project.repositories.UserRepository;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
public class BookshopSteps {
	
	private static int port = Integer.parseInt(System.getProperty("server.port", "8080"));
	
	private static String baseUrl = "http://localhost:" + port;
	
	private WebDriver webDriver;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	BookRepository bookRepository;
	
	@Before
	public void setUpWebDriver() {
		baseUrl = "http://localhost:" + port;
		webDriver = new ChromeDriver();
		userRepository.deleteAll();
		bookRepository.deleteAll();
		
	}
	
	@After
	public void closeBrowser() {
		webDriver.quit();
	}

	@Given("I am on the Registration page")
	public void i_am_on_the_Registration_page() {
	    webDriver.get(baseUrl + "/registration");
	}

	@When("I insert {string} into email field, {string} into username field and {string} into password field")
	public void i_insert_into_email_field_into_username_field_and_into_password_field(String email, String username, String password) {
	    webDriver.findElement(By.name("registration_form"));
	    
	    webDriver.findElement(By.name("email")).sendKeys(email);
	    webDriver.findElement(By.name("username")).sendKeys(username);
	    webDriver.findElement(By.name("password")).sendKeys(password);
	    
	}

	@When("I click the {string} button")
	public void i_click_the_button(String button) throws Exception {
		webDriver.findElement(By.name(button)).click();
		Thread.sleep(1500);
	}
	
	@Then("I am on the {string} page")
	public void i_am_on_the_page(String pageTitle) {
		assertThat(webDriver.getTitle()).isEqualTo(pageTitle);
	}
	
	@Then("{string} message is shown")
	public void message_is_shown(String message) {
		assertThat(webDriver.getPageSource()).contains(message);
	}
	
	@When("I click on {string} link")
	public void i_click_on_link(String linkName) throws Exception {
	    webDriver.findElement(By.linkText(linkName)).click();
	    Thread.sleep(1500);	    
	}

	@When("I insert {string} into email field and {string} into password field")
	public void i_insert_into_email_field_and_into_password_field(String email, String password) {
	    webDriver.findElement(By.name("login_form"));
	    
	    webDriver.findElement(By.name("email")).sendKeys(email);
	    webDriver.findElement(By.name("password")).sendKeys(password);
	}
	
	@Then("I am on the Home Page")
	public void i_am_on_the_Home_Page() {
	    assertThat(webDriver.getTitle()).isEqualTo("Home");
	}
	
	@When("I insert {string} in title field, {string} in author field and {string} in price field")
	public void i_insert_in_title_field_in_author_field_and_in_price_field(String title, String author, String price) throws Exception {
	    assertThat(webDriver.getTitle()).isEqualTo("Edit Page");
		
		webDriver.findElement(By.name("book_form"));
		
		webDriver.findElement(By.name("title")).sendKeys(title);
	    webDriver.findElement(By.name("author")).sendKeys(author);
	    webDriver.findElement(By.name("price")).clear();
	    webDriver.findElement(By.name("price")).sendKeys(price);

	}
	
	@Then("The {string} is shown and it contains a book with {string}, {string}, and price {string}")
	public void the_is_shown_and_it_contains_a_book_with_and_price(String table, String title, String author, String price) {
	    webDriver.findElement(By.id(table));
	    
	    assertThat(webDriver.findElement(By.id(table)).getText()).
	    		contains(title, author, price);
	}
	
	@When("I update the author field with {string} and the price field with {string}")
	public void i_update_the_author_field_with_and_the_price_field_with(String updatedAuthor, String updatedPrice) {
		
		webDriver.findElement(By.name("book_form"));
		
		webDriver.findElement(By.name("author")).clear();
		webDriver.findElement(By.name("author")).sendKeys(updatedAuthor);
		webDriver.findElement(By.name("price")).clear();
		webDriver.findElement(By.name("price")).sendKeys(updatedPrice);
	}
	
	@When("I insert {string} in the search field")
	public void i_insert_in_the_search_field(String searchedTitle) {
		webDriver.findElement(By.name("search_form"));
		
		webDriver.findElement(By.name("title_searched")).clear();
	    webDriver.findElement(By.name("title_searched")).sendKeys(searchedTitle);
	    
	}
}
