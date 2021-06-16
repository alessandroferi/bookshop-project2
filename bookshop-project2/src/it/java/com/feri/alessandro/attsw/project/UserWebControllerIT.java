package com.feri.alessandro.attsw.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.feri.alessandro.attsw.project.model.User;
import com.feri.alessandro.attsw.project.repositories.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserWebControllerIT {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private MockMvc mvc;

	@Before
	public void setUp() {
		
		mvc = MockMvcBuilders.
				webAppContextSetup(context).
					apply(springSecurity()).
						build();
		
		userRepository.deleteAll();
	}
	
	@After
	public void tearDown() {
		userRepository.deleteAll();
	}
	
	
	@Test
	public void test_returnLoginPageView() throws Exception {		
		ModelAndViewAssert.assertViewName(
				mvc.perform(get("/login")).
					andReturn().getModelAndView(), "login");
	}
	
	@Test
	public void test_returnRegistrationPageView() throws Exception {
		ModelAndViewAssert.assertViewName(
				mvc.perform(get("/registration")).
					andReturn().getModelAndView(), "registration");
	}
	
	@Test
	public void test_createNewUser_shouldReturnResultPageAndCreateNewUser() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(post("/saveUser")
				.param("email", "email@gmail")
				.param("username", "test")
				.param("password", "password")).andReturn().getModelAndView(), "registrationResult");
		
		assertEquals(1, userRepository.findAll().size());
		
		User saved = userRepository.findAll().get(0);
		
		assertNotNull(saved.getId());
		assertEquals("email@gmail", saved.getEmail());
		assertEquals("test", saved.getUsername());
		assertTrue(passwordEncoder.matches("password", saved.getPassword()));
		
	}
	
	@Test
	public void test_createNewUser_WhenEmailAlreadyExistShouldNotSaveTheUser_andShouldReturnResultPage() throws Exception {
		User saved = new User(null, "already_exist@gmail", "username", "password");
		
		userRepository.save(saved);
		
		ModelAndViewAssert.assertViewName(mvc.perform(post("/saveUser")
				.param("email", "already_exist@gmail")
				.param("username", "not_exist")
				.param("password", "pass")).andReturn().getModelAndView(), "registrationResult");
		
		assertEquals(1, userRepository.findAll().size());
		assertThat(userRepository.findAll()).containsExactly(saved);
		
	}
	
	@Test
	public void test_createNewUser_WhenUsernamaAlreadyExistShouldNotSaveTheUser_andShouldReturnResultPage() throws Exception {
		User saved = new User(null, "email@gmail", "already_exist", "password");
		
		userRepository.save(saved);
		
		ModelAndViewAssert.assertViewName(mvc.perform(post("/saveUser")
				.param("email", "not_exist@gmail")
				.param("username", "already_exist")
				.param("password", "pass")).andReturn().getModelAndView(), "registrationResult");
		
		assertEquals(1, userRepository.findAll().size());
		assertThat(userRepository.findAll()).containsExactly(saved);
	}
	
}
