package com.feri.alessandro.attsw.project.web;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;

import com.feri.alessandro.attsw.project.exception.EmailExistException;
import com.feri.alessandro.attsw.project.exception.UsernameExistException;
import com.feri.alessandro.attsw.project.model.User;
import com.feri.alessandro.attsw.project.services.UserService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserWebController.class)
public class UserWebControllerTest {

	private static final String EMPTY_MESSAGE = "";

	@MockBean
	private UserService userService;
	
	@Autowired
	private MockMvc mvc;
	
	@Test
	public void test_status200Login() throws Exception {
		mvc.perform(get("/login")).
			andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void test_returnLoginViewName() throws Exception {
		ModelAndViewAssert.assertViewName(
				mvc.perform(get("/login")).andReturn().getModelAndView(), "login");
	}
	
	@Test
	public void test_status200Registration() throws Exception {
		mvc.perform(get("/registration")).
			andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void test_returnRegistrationViewName() throws Exception {
		ModelAndViewAssert.assertViewName(
				mvc.perform(get("/registration")).andReturn().getModelAndView(), "registration");
	}
	
	
	@Test
	public void test_createNewUser() throws Exception {
		mvc.perform(post("/saveUser")
				.param("email", "email@gmail")
				.param("username", "username")
				.param("password", "password")).
		andExpect(view().name("registrationResult")).
		andExpect(model().attribute("message", EMPTY_MESSAGE));
		
		verify(userService).findUserByEmail("email@gmail");
		verify(userService).findUserByUsername("username");
		
		verify(userService).saveUser(new User(null, "email@gmail", "username", "password"));
	}
	
	@Test
	public void test_createNewUser_whenEmailAlreadyExist() throws Exception {
		when(userService.findUserByEmail("tested_email@gmail")).thenThrow(EmailExistException.class);
		
		mvc.perform(post("/saveUser")
				.param("email", "tested_email@gmail")
				.param("username", "username")
				.param("password", "password"))
			.andExpect(view().name("registrationResult"))
			.andExpect(model().attribute("message", "There is already a user registered with the email provided. "
				+ "Please, try with another email address."))
			.andExpect(status().is(409));
		
		verify(userService).findUserByEmail("tested_email@gmail");
		verifyNoMoreInteractions(userService);
	}
	
	@Test
	public void test_createNewUser_WhenUsernameAlreadyExist() throws Exception {
		when(userService.findUserByUsername("tested_username")).thenThrow(UsernameExistException.class);
		
		mvc.perform(post("/saveUser").
				param("email", "email@gmail").
				param("username", "tested_username").
				param("password", "password")).
			andExpect(view().name("registrationResult")).
			andExpect(model().attribute("message", "There is already a user registered with the username provided. "
				+ "Please, try with another username.")).
			andExpect(status().is(409));
		
		verify(userService).findUserByUsername("tested_username");
	}
}
