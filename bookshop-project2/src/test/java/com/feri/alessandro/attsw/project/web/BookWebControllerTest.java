package com.feri.alessandro.attsw.project.web;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigInteger;
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
import com.feri.alessandro.attsw.project.model.Book;
import com.feri.alessandro.attsw.project.services.BookService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = BookWebController.class)
public class BookWebControllerTest {
	
	private static final String BOOK_NOT_FOUND = "Book not found!";

	private static final String EMPTY_MESSAGE = "";

	@MockBean
	private BookService bookService;
	
	@Autowired
	private MockMvc mvc;
	
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
		 
		 verify(bookService).getAllBooks();
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
		 
		 verify(bookService).getAllBooks();
		 verifyNoMoreInteractions(bookService);	 
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_editBookById_WhenIdIsNotFound() throws Exception {
		 when(bookService.getBookById(BigInteger.valueOf(1))).thenThrow(BookNotFoundException.class);
		 
		 mvc.perform(get("/edit/1"))
		 	.andExpect(view().name("bookNotFound"))
		 	.andExpect(model().attribute("book", nullValue()))
		 	.andExpect(model().attribute("message", BOOK_NOT_FOUND))
		 	.andExpect(status().is(404));
		 
		 verify(bookService).getBookById(BigInteger.valueOf(1));
		 verifyNoMoreInteractions(bookService);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_editBookById_WhenIdIsFound() throws Exception {
		 Book found = new Book(BigInteger.valueOf(1), "title", "author", 10.0);
		 
		 when(bookService.getBookById(BigInteger.valueOf(1))).thenReturn(found);
		 
		 mvc.perform(get("/edit/1"))
		 	.andExpect(view().name("edit"))
		 	.andExpect(model().attribute("book", found))
		 	.andExpect(model().attribute("message", EMPTY_MESSAGE));
		 
		 verify(bookService).getBookById(BigInteger.valueOf(1));
		 verifyNoMoreInteractions(bookService);
		 
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_editNewBook() throws Exception {
		 mvc.perform(get("/new"))
		 	.andExpect(view().name("edit"))
		 	.andExpect(model().attribute("book", new Book()))
		 	.andExpect(model().attribute("message", EMPTY_MESSAGE));
		 
		 verifyNoInteractions(bookService);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_PostBookWithoutId_ShouldInsertNewBook() throws Exception {
		 mvc.perform(post("/save")
				 .param("title", "newTitle")
				 .param("author", "newAuthor")
				 .param("price", "10"))
		 	.andExpect(view().name("redirect:/"));
		 	
			verify(bookService).insertNewBook(
					new Book(null, "newTitle", "newAuthor", 10.0));
			verifyNoMoreInteractions(bookService);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_PostBookBookWithId_ShouldUpdateExistingBook() throws Exception {
		 mvc.perform(post("/save")
				 .param("id", "1")
				 .param("title", "updatedTitle")
				 .param("author", "updatedAuthor")
				 .param("price", "10"))
		 	.andExpect(view().name("redirect:/"));

		 verify(bookService).editBookById(
				 BigInteger.valueOf(1), new Book(BigInteger.valueOf(1), "updatedTitle", "updatedAuthor", 10.0));
		 verifyNoMoreInteractions(bookService);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_PostBookWhenIdNotFound() throws Exception {
		 Book replacement = new Book(BigInteger.valueOf(1), "title", "author", 10.0);
		 
		 when(bookService.editBookById(BigInteger.valueOf(1), replacement)).thenThrow(BookNotFoundException.class);
		 
		 mvc.perform(post("/save")
				 .param("id", "1")
				 .param("title", "title")
				 .param("author", "author")
				 .param("price", "10"))
		 	.andExpect(view().name("bookNotFound"))
		 	.andExpect(model().attribute("book", nullValue()))
		 	.andExpect(model().attribute("message", BOOK_NOT_FOUND))
		 	.andExpect(status().is(404));	 	
		 
		 verify(bookService).editBookById(BigInteger.valueOf(1), replacement);
		 verifyNoMoreInteractions(bookService);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_Search_WithShouldShowSearchedBook() throws Exception {
		 String search = "title";
		 
		 List<Book> books = asList(new Book(null, "title", "author", 10.0));
		 
		 when(bookService.getBookByTitle(search)).thenReturn(books);
		 
		 mvc.perform(get("/search")
				 .param("title_searched", search))
		 	.andExpect(model().attribute("books", books))
		 	.andExpect(model().attribute("message", EMPTY_MESSAGE))
		 	.andExpect(view().name("search"));
		 
		 verify(bookService).getBookByTitle(search);
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
		 
		 verify(bookService).getBookByTitle(search);
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
		 Book toDelete = new Book(BigInteger.valueOf(1), "title", "author", 10.0);
		 
		 when(bookService.getBookById(BigInteger.valueOf(1))).thenReturn(toDelete);
		 
		 String deletedId = BigInteger.valueOf(1).toString();
		 
		 mvc.perform(get("/delete")
				 .param("id", deletedId))
		 	.andExpect(view().name("redirect:/"));
		 
		 verify(bookService).getBookById(BigInteger.valueOf(1));
		 verify(bookService).deleteOneBook(toDelete);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_deleteBook_whenBookNotFound() throws Exception {
		 when(bookService.getBookById(BigInteger.valueOf(1))).thenThrow(BookNotFoundException.class);
		 
		 String deletedId = BigInteger.valueOf(1).toString();
		 
		 mvc.perform(get("/delete").
				 param("id", deletedId))
		 	.andExpect(view().name("bookNotFound"))
		 	.andExpect(model().attribute("message", BOOK_NOT_FOUND))
		 	.andExpect(status().is(404));
		 
		 verify(bookService).getBookById(BigInteger.valueOf(1));
		 verifyNoMoreInteractions(bookService);
	 }
	 
	 @Test
	 @WithMockUser
	 public void test_deleteAll() throws Exception {
		 mvc.perform(get("/deleteAll"))
		 	.andExpect(view().name("redirect:/"));
		 
		 verify(bookService).deleteAllBooks();
		 verifyNoMoreInteractions(bookService);
	 }

}
