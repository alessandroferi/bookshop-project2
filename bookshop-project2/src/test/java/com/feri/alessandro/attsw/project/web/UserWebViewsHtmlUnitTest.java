package com.feri.alessandro.attsw.project.web;

import static com.gargoylesoftware.htmlunit.WebAssert.assertElementPresent;
import static com.gargoylesoftware.htmlunit.WebAssert.assertFormPresent;
import static com.gargoylesoftware.htmlunit.WebAssert.assertInputPresent;
import static com.gargoylesoftware.htmlunit.WebAssert.assertLinkPresentWithText;
import static com.gargoylesoftware.htmlunit.WebAssert.assertTextPresent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.feri.alessandro.attsw.project.exception.EmailExistException;
import com.feri.alessandro.attsw.project.exception.UsernameExistException;
import com.feri.alessandro.attsw.project.model.User;
import com.feri.alessandro.attsw.project.services.UserService;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserWebController.class)
public class UserWebViewsHtmlUnitTest {

	@MockBean
	private UserService userService;
	
	@Autowired
	private WebClient webClient;
	
	@Test
	public void test_LoginPageTitle() throws Exception {
		HtmlPage page = webClient.getPage("/login");
		assertThat(page.getTitleText()).isEqualTo("Login");
	}
	
	@Test
	public void test_LoginPageStructure() throws Exception {
		HtmlPage page = webClient.getPage("/login");
		
		assertTextPresent(page, "Sign in");
		assertFormPresent(page, "login_form");
		assertInputPresent(page, "username");
		assertInputPresent(page, "password");
		assertElementPresent(page, "login_button");
		assertElementPresent(page, "registration_button");
		assertLinkPresentWithText(page, "Register");

	}
	
	@Test
	public void test_RegistrationPageTitle() throws Exception {
		HtmlPage page = webClient.getPage("/registration");
		assertThat(page.getTitleText()).isEqualTo("Registration Page");
	}
	
	@Test
	public void test_RegistrationPageStructure() throws Exception {
		HtmlPage page = webClient.getPage("/registration");
		
		assertTextPresent(page, "Register Here");
		assertFormPresent(page, "registration_form");
		assertTextPresent(page, "Email address:");
		assertInputPresent(page, "email");
		assertTextPresent(page, "Username:");
		assertInputPresent(page, "username");
		assertTextPresent(page, "Password:");
		assertInputPresent(page, "password");
		assertElementPresent(page, "registration_button");
		assertLinkPresentWithText(page, "Login Page");
		
	}
	
	@Test
	public void test_RegistrationWhenEmailAlreadyExist() throws Exception {
		when(userService.findUserByEmail("email_exist@gmail")).
			thenThrow(EmailExistException.class);
		
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		
		HtmlPage page = webClient.getPage("/registration");
		
		HtmlForm form = page.getFormByName("registration_form");
		
		form.getInputByName("email").setValueAttribute("email_exist@gmail");
		form.getInputByName("username").setValueAttribute("username");
		form.getInputByName("password").setValueAttribute("password");
		
		HtmlPage result = form.getButtonByName("Register").click();
		
		assertThat(result.getTitleText()).isEqualTo("Result");
		assertThat(result.getBody().getTextContent()).contains(
				"There is already a user registered with the email provided. "
				+ "Please, try with another email address.", "ERROR 409");
		assertLinkPresentWithText(result, "Go back to Registration page");
		
		verify(userService).findUserByEmail("email_exist@gmail");
		
	}
	
	@Test
	public void test_RegistrationWhenUsernameAlreadyExist() throws Exception {
		when(userService.findUserByUsername("username_exist")).
			thenThrow(UsernameExistException.class);
	
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
	
		HtmlPage page = webClient.getPage("/registration");
	
		HtmlForm form = page.getFormByName("registration_form");
	
		form.getInputByName("email").setValueAttribute("email@gmail");
		form.getInputByName("username").setValueAttribute("username_exist");
		form.getInputByName("password").setValueAttribute("password");
	
		HtmlPage result = form.getButtonByName("Register").click();
	
		assertThat(result.getTitleText()).isEqualTo("Result");
		assertThat(result.getBody().getTextContent()).contains(
			"There is already a user registered with the username provided. "
			+ "Please, try with another username.", "ERROR 409");
		assertLinkPresentWithText(result, "Go back to Registration page");
	
		verify(userService).findUserByUsername("username_exist");
	}
	
	@Test
	public void test_SuccessfullRegistration() throws Exception {
		when(userService.findUserByEmail("email@gmail")).thenReturn(null);
		when(userService.findUserByUsername("username")).thenReturn(null);
		
		HtmlPage page = webClient.getPage("/registration");
		
		HtmlForm form = page.getFormByName("registration_form");
		
		form.getInputByName("email").setValueAttribute("email@gmail");
		form.getInputByName("username").setValueAttribute("username");
		form.getInputByName("password").setValueAttribute("password");
		
		HtmlPage result = form.getButtonByName("Register").click();
		
		verify(userService).findUserByEmail("email@gmail");
		verify(userService).findUserByUsername("username");
		verify(userService).saveUser(new User(null, "email@gmail", "username", "password"));
		assertThat(result.getTitleText()).isEqualTo("Result");
		assertThat(result.getBody().getTextContent()).contains(
				"You have successfully registered!");
		assertLinkPresentWithText(result, "Login Page");

	}
}
