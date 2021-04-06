package com.feri.alessandro.attsw.project.web;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;

import com.feri.alessandro.attsw.project.exception.BookNotFoundException;
import com.feri.alessandro.attsw.project.exception.EmailExistException;
import com.feri.alessandro.attsw.project.exception.UsernameExistException;
import com.feri.alessandro.attsw.project.model.Book;
import com.feri.alessandro.attsw.project.model.User;
import com.feri.alessandro.attsw.project.services.BookService;
import com.feri.alessandro.attsw.project.services.UserService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = BookshopWebController.class)
public class BookshopWebControllerTest {

	private static final String BOOK_NOT_FOUND = "Book not found!";

	private static final String EMPTY_MESSAGE = "";

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private BookService bookService;
	
	@MockBean
	private UserService userService;
	
	
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
	
	@Test
	@WithMockUser
	public void test_status200() throws Exception {
		mvc.perform(get("/")).
			andExpect(status().is2xxSuccessful());
	}
	
	@Test
	@WithMockUser
	public void test_returnHomeView() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/")).
					andReturn().getModelAndView(), "index");
	}
	
	 @Test
	 @WithMockUser
	 public void test_HomeView_showsMessageWhenThereAreNoBooks() throws Exception {
		 when(bookService.getAllBooks()).thenReturn(Collections.emptyList());
		 
		 mvc.perform(get("/"))
		 	.andExpect(view().name("index"))
		 	.andExpect(model().attribute("books", Collections.emptyList()))
		 	.andExpect(model().attribute("message", "There are no books."));
		 
		 verify(bookService, times(1)).getAllBooks();
		 verifyNoMoreInteractions(bookService);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_HomeView_showsBooks() throws Exception {
		 List<Book> books = asList(new Book(null, "title", "author", 10.0));
		 
		 when(bookService.getAllBooks()).thenReturn(books);
		 
		 mvc.perform(get("/"))
		 	.andExpect(view().name("index"))
		 	.andExpect(model().attribute("books", books))
		 	.andExpect(model().attribute("message", EMPTY_MESSAGE));
		 
		 verify(bookService, times(1)).getAllBooks();
		 verifyNoMoreInteractions(bookService);	 
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_editBookById_WhenIdIsNotFound() throws Exception {
		 when(bookService.getBookById(1L)).thenThrow(BookNotFoundException.class);
		 
		 mvc.perform(get("/edit/1"))
		 	.andExpect(view().name("bookNotFound"))
		 	.andExpect(model().attribute("book", nullValue()))
		 	.andExpect(model().attribute("message", BOOK_NOT_FOUND))
		 	.andExpect(status().is(404));
		 
		 verify(bookService, times(1)).getBookById(1L);
		 verifyNoMoreInteractions(bookService);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_editBookById_WhenIdIsFound() throws Exception {
		 Book found = new Book(1L, "title", "author", 10.0);
		 
		 when(bookService.getBookById(1L)).thenReturn(found);
		 
		 mvc.perform(get("/edit/1"))
		 	.andExpect(view().name("edit"))
		 	.andExpect(model().attribute("book", found))
		 	.andExpect(model().attribute("message", EMPTY_MESSAGE));
		 
		 verify(bookService, times(1)).getBookById(1L);
		 verifyNoMoreInteractions(bookService);
		 
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_editNewBook() throws Exception {
		 mvc.perform(get("/new"))
		 	.andExpect(view().name("edit"))
		 	.andExpect(model().attribute("book", new Book()))
		 	.andExpect(model().attribute("message", EMPTY_MESSAGE));
		 
		 verifyZeroInteractions(bookService);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_PostBookWithoutId_ShouldInsertNewBook() throws Exception {
		 mvc.perform(post("/save")
				 .param("title", "testedTitle")
				 .param("author", "testedAuthor")
				 .param("price", "10"))
		 	.andExpect(view().name("redirect:/"));
		 	
			verify(bookService, times(1)).insertNewBook(
					new Book(null, "testedTitle", "testedAuthor", 10.0));
			verifyNoMoreInteractions(bookService);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_PostBookBookWithId_ShouldUpdateExistingBook() throws Exception {
		 mvc.perform(post("/save")
				 .param("id", "1")
				 .param("title", "testedTitle")
				 .param("author", "testedAuthor")
				 .param("price", "10"))
		 	.andExpect(view().name("redirect:/"));

		 verify(bookService, times(1)).editBookById(
				 1L, new Book(1L, "testedTitle", "testedAuthor", 10.0));
		 verifyNoMoreInteractions(bookService);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_PostBookWhenIdNotFound() throws Exception {
		 Book replacement = new Book(1L, "title", "author", 10.0);
		 
		 when(bookService.editBookById(1L, replacement)).thenThrow(BookNotFoundException.class);
		 
		 mvc.perform(post("/save")
				 .param("id", "1")
				 .param("title", "title")
				 .param("author", "author")
				 .param("price", "10"))
		 	.andExpect(view().name("bookNotFound"))
		 	.andExpect(model().attribute("book", nullValue()))
		 	.andExpect(model().attribute("message", BOOK_NOT_FOUND))
		 	.andExpect(status().is(404));	 	
		 
		 verify(bookService, times(1)).editBookById(1L, replacement);
		 verifyNoMoreInteractions(bookService);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_Search_ShouldShowSearchedBook() throws Exception {
		 String search = "title";
		 
		 Book searched =  new Book(1L, "title", "author", 10.0);
		 
		 when(bookService.getBookByTitle(search)).thenReturn(searched);
		 
		 mvc.perform(get("/search")
				 .param("title_searched", search))
		 	.andExpect(model().attribute("book", equalTo(searched)))
		 	.andExpect(model().attribute("message", EMPTY_MESSAGE))
		 	.andExpect(view().name("search"));
		 
		 verify(bookService, times(1)).getBookByTitle(search);
		 verifyNoMoreInteractions(bookService);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_SearchWithBookNotFound() throws Exception {
		 String search = "not_found";
		 
		 when(bookService.getBookByTitle(search)).thenThrow(BookNotFoundException.class);
		 
		 mvc.perform(get("/search")
				 .param("title_searched", search))
		 	.andExpect(model().attribute("message", BOOK_NOT_FOUND))
		 	.andExpect(model().attribute("book", nullValue()))
		 	.andExpect(view().name("bookNotFound"))
		 	.andExpect(status().is(404));
		 
		 verify(bookService, times(1)).getBookByTitle(search);
		 verifyNoMoreInteractions(bookService);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_Search_WithEmptySearchField_shouldShowErrorMessage() throws Exception {
		 String search = "";
		 
		 mvc.perform(get("/search")
				 .param("title_searched", search))
		 	.andExpect(model().attribute("message", "Error! Please, insert a valid title."))
		 	.andExpect(view().name("search"));
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_deleteBook() throws Exception {
		 Book toDelete = new Book(1L, "title", "author", 10.0);
		 
		 when(bookService.getBookById(1L)).thenReturn(toDelete);
		 
		 mvc.perform(get("/delete?id=1"))
		 	.andExpect(view().name("redirect:/"));
		 
		 verify(bookService, times(1)).getBookById(1L);
		 verify(bookService, times(1)).deleteOneBook(toDelete);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_deleteBook_whenBookNotFound() throws Exception {
		 when(bookService.getBookById(1L)).thenThrow(BookNotFoundException.class);
		 
		 mvc.perform(get("/delete?id=1"))
		 	.andExpect(view().name("bookNotFound"))
		 	.andExpect(model().attribute("message", BOOK_NOT_FOUND))
		 	.andExpect(status().is(404));
		 
		 verify(bookService, times(1)).getBookById(1L);
		 verifyNoMoreInteractions(bookService);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_deleteAll() throws Exception {
		 mvc.perform(get("/deleteAll"))
		 	.andExpect(view().name("redirect:/"));
		 
		 verify(bookService, times(1)).deleteAllBooks();
		 verifyNoMoreInteractions(bookService);
	 }

}
