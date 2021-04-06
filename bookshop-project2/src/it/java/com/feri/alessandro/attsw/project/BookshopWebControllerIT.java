package com.feri.alessandro.attsw.project;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.feri.alessandro.attsw.project.model.Book;
import com.feri.alessandro.attsw.project.model.User;
import com.feri.alessandro.attsw.project.repositories.BookRepository;
import com.feri.alessandro.attsw.project.repositories.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookshopWebControllerIT {
	
	private static final String EMPTY_MESSAGE = "";

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private BookRepository bookRepository;
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private WebApplicationContext context;
	
	private MockMvc mvc;

	
	@Before
	public void setUp() {
		
		mvc = MockMvcBuilders.
				webAppContextSetup(context).
					apply(springSecurity()).
						build();
		
		userRepository.deleteAll();
		bookRepository.deleteAll();
		bookRepository.flush();
		
	}
	
	@After
	public void tearDown() {
		userRepository.deleteAll();
		bookRepository.deleteAll();
	}
	
	
	@Test
	public void test_returnLoginPageView() throws Exception {		
		ModelAndViewAssert.assertViewName(
				mvc.perform(get("/login")).
					andReturn().getModelAndView(), "login");
	}
	
	@Test
	public void test_returnRegiatrationPageView() throws Exception {
		ModelAndViewAssert.assertViewName(
				mvc.perform(get("/registration")).
					andReturn().getModelAndView(), "registration");
	}
	
	@Test
	public void test_createNewUser_shouldReturnResultPage() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(post("/saveUser")
				.param("email", "email@gmail")
				.param("username", "test")
				.param("password", "password")).andReturn().getModelAndView(), "registrationResult");
		
		assertEquals(userRepository.findAll().size(), 1);
		
		User saved = userRepository.findAll().get(0);
		
		assertEquals(saved.getEmail(), "email@gmail");
		assertEquals(saved.getUsername(), "test");
		assertThat(bCryptPasswordEncoder.matches("password", saved.getPassword()));
		
	}
	
	@Test
	public void test_createNewUser_WhenEmailAlreadyExistShouldNotSaveTheUser_andShouldReturnResultPage() throws Exception {
		User saved = new User();
		saved.setEmail("already_exist@gmail");
		saved.setUsername("username");
		saved.setPassword("password");
		
		userRepository.save(saved);
		
		ModelAndViewAssert.assertViewName(mvc.perform(post("/saveUser")
				.param("email", "already_exist@gmail")
				.param("username", "user")
				.param("password", "pass")).andReturn().getModelAndView(), "registrationResult");
		
		assertEquals(userRepository.findAll().size(), 1);
		assertThat(userRepository.findAll()).containsExactly(saved);
		
		
	}
	
	@Test
	public void test_createNewUser_WhenUsernamaAlreadyExistShouldNotSaveTheUser_andShouldReturnResultPage() throws Exception {
		User saved = new User();
		saved.setEmail("email@gmail");
		saved.setUsername("already_exist");
		saved.setPassword("password");
		
		userRepository.save(saved);
		
		ModelAndViewAssert.assertViewName(mvc.perform(post("/saveUser")
				.param("email", "not_exist@gmail")
				.param("username", "already_exist")
				.param("password", "pass")).andReturn().getModelAndView(), "registrationResult");
		
		assertEquals(userRepository.findAll().size(), 1);
		assertThat(userRepository.findAll()).containsExactly(saved);
	}
	
	@Test
	@WithMockUser
	public void test_returnHomePageView() throws Exception {
		ModelAndViewAssert.assertViewName(
				mvc.perform(get("/")).
					andReturn().getModelAndView(), "index");
	}
	
	@Test
	@WithMockUser
	public void test_homePage_shouldContainEmptyBookList() throws Exception {
		mvc.perform(get("/")).
			andExpect(view().name("index")).
			andExpect(model().attribute("books", Collections.emptyList())).
			andExpect(model().attribute("message", "There are no books."));
		
		assertThat(bookRepository.findAll()).isEmpty();
	}
	
	@Test
	@WithMockUser
	public void test_homePage_shouldContainBooks() throws Exception {
		List<Book> books = asList(
				new Book(null, "title1", "author1", 10.0),
				new Book(null, "title2", "author2", 15.0),
				new Book(null, "title3", "author3", 20.0));
		
		bookRepository.saveAll(books);
	
		mvc.perform(get("/")).
			andExpect(view().name("index")).
			andExpect(model().attribute("books", books)).
			andExpect(model().attribute("message", EMPTY_MESSAGE));
			
	}
	
	@Test
	@WithMockUser
	public void test_editBook_withExistingBookIntoRepository() throws Exception {
		Book saved = new Book(null, "title", "author", 10.0);
		
		bookRepository.save(saved);
		
		Long id = bookRepository.findAll().get(0).getId();
		
		mvc.perform(get("/edit/" + id)).
			andExpect(view().name("edit")).
			andExpect(model().attribute("book", saved)).
			andExpect(model().attribute("message", EMPTY_MESSAGE));
	}
	
	@Test
	@WithMockUser
	public void test_editBook_withNonExistingBookIntoRepository_shouldReturnBookNotFoundView() throws Exception {
		
		mvc.perform(get("/edit/1")).
			andExpect(view().name("bookNotFound")).
			andExpect(model().attribute("book", nullValue())).
			andExpect(model().attribute("message", "Book not found!"));
		
		assertThat(userRepository.findAll()).isEmpty();
	}
	
	@Test
	@WithMockUser
	public void test_newBook_shouldReturnEditView() throws Exception {
		
		mvc.perform(get("/new")).
			andExpect(view().name("edit")).
			andExpect(model().attribute("book", new Book())).
			andExpect(model().attribute("message", EMPTY_MESSAGE));
		
	}
	
	@Test
	@WithMockUser
	public void test_saveWithoutId_shouldInsertBookIntoRepository_andRedirectToHomePage() throws Exception {
		
		assertThat(bookRepository.findAll()).isEmpty();
		
		mvc.perform(post("/save")
				.param("title", "title1")
				.param("author", "author1")
				.param("price", "10.0")).
			andExpect(view().name("redirect:/"));
		
		assertEquals(bookRepository.findAll().size(), 1);
	}
	
	@Test
	@WithMockUser
	public void test_saveWithExistingId_shouldUpdateBookIntoRepository_andRedirectToHomePage() throws Exception {
		Book saved = new Book(null, "original_title", "original_author", 10.0);
		
		bookRepository.save(saved);
		
		assertEquals(bookRepository.findAll().size(), 1);
		
		Long id = bookRepository.findAll().get(0).getId();
		
		mvc.perform(post("/save")
				.param("id", id.toString())
				.param("title", "modified_title")
				.param("author", "modified_author")
				.param("price", "15.0")).
			andExpect(view().name("redirect:/"));
		
		assertEquals(bookRepository.findAll().size(), 1);
		
		Book updated = bookRepository.findAll().get(0);
		
		assertEquals(saved.getId(), updated.getId());
		assertThat(updated.getTitle()).isEqualTo("modified_title");
		assertThat(updated.getAuthor()).isEqualTo("modified_author");
		assertThat(updated.getPrice()).isEqualTo(15.0);
	}
	
	@Test
	@WithMockUser
	public void test_searchView() throws Exception {
		Book saved = new Book(null, "existing_title", "author", 10.0);
		
		bookRepository.save(saved);
		
		mvc.perform(get("/search").
				param("title_searched", saved.getTitle())).
			andExpect(view().name("search")).
			andExpect(model().attribute("book", saved)).
			andExpect(model().attribute("message", EMPTY_MESSAGE));
		
		assertThat(bookRepository.findByTitle(saved.getTitle())).isPresent();
	}
	
	@Test
	@WithMockUser
	public void test_searchWithEmptyText_shouldShowError() throws Exception {
		mvc.perform(get("/search").
				param("title_searched", "")).
			andExpect(view().name("search")).
			andExpect(model().attribute("message", "Error! Please, insert a valid title."));
	}
	
	@Test
	@WithMockUser
	public void test_deleteBook_shouldDeleteFromRepository_andRedirectToHomepage() throws Exception {
		Book saved = new Book(null, "title", "author", 10.0);
		bookRepository.save(saved);
		
		assertThat(bookRepository.findAll()).containsExactly(saved);
		
		Long id = bookRepository.findAll().get(0).getId();
		
		mvc.perform(get("/delete?id=" + id)).
			andExpect(view().name("redirect:/"));
		
		assertThat(bookRepository.findAll()).isEmpty();
	}
	
	@Test
	@WithMockUser
	public void test_deleteAll_souldDeleteAllBooksFromRepository_andRedirectToHomepage() throws Exception {
		List<Book> books = asList(
				new Book(null, "title1", "author1", 10.0),
				new Book(null, "title2", "author2", 15.0),
				new Book(null, "title3", "author3", 20.0));
		
		bookRepository.saveAll(books);
		
		assertEquals(bookRepository.findAll().size(), 3);
		
		mvc.perform(get("/deleteAll")).
			andExpect(view().name("redirect:/"));
		
		assertThat(bookRepository.findAll()).isEmpty();
	}
	
}
