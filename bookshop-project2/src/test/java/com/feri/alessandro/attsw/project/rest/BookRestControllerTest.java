package com.feri.alessandro.attsw.project.rest;

import static java.util.Arrays.asList;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigInteger;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.feri.alessandro.attsw.project.exception.BookNotFoundException;
import com.feri.alessandro.attsw.project.exception.BookshopRestExceptionHandler;
import com.feri.alessandro.attsw.project.model.Book;
import com.feri.alessandro.attsw.project.services.BookService;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.config.MockMvcConfig;
import io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig;

@RunWith(MockitoJUnitRunner.class)
public class BookRestControllerTest {

	private static final String BOOK_NOT_FOUND = "Book not found!";

	@Mock
	private BookService bookService;
	
	@InjectMocks
	private BookRestController bookRestController;
	
	
	@Before
	public void setUp() {
		RestAssuredMockMvc.standaloneSetup(
				MockMvcBuilders
					.standaloneSetup(bookRestController).
						setControllerAdvice(new BookshopRestExceptionHandler()));
	}
	
	@Test
	public void testGET_allBooksEmpty() {
		when(bookService.getAllBooks()).thenReturn(Collections.emptyList());
		
		given().config(noSecurity()).
		when().
			get("/api/books").
		then().
			statusCode(200).
			assertThat().
				contentType(MediaType.APPLICATION_JSON_VALUE).
				body(is(equalTo("[]"))
			);
		
		verify(bookService, times(1)).getAllBooks();
		verifyNoMoreInteractions(bookService);
	}
	
	@Test
	public void testGET_allBooksNotEmpty() {
		Book testBook1 = new Book(BigInteger.valueOf(1), "firstTitle", "author1", 7.0);
		Book testBook2 = new Book(BigInteger.valueOf(2), "secondTitle", "author2", 9.0);
		when(bookService.getAllBooks()).thenReturn(asList(testBook1, testBook2));
		
		given().config(noSecurity()).
		when().
			get("/api/books").
		then().
			statusCode(200).
			assertThat().
				contentType(MediaType.APPLICATION_JSON_VALUE).
				body("id[0]", equalTo(1),
					 "title[0]", equalTo("firstTitle"),
					 "author[0]", equalTo("author1"),
					 "price[0]", equalTo(7.0f),
					 "id[1]", equalTo(2),
					 "title[1]", equalTo("secondTitle"),
					 "author[1]", equalTo("author2"),
					 "price[1]", equalTo(9.0f)
				);
		
		verify(bookService, times(1)).getAllBooks();
		verifyNoMoreInteractions(bookService);
	}
	
	@Test
	public void testGET_getBookById_WithNonExistingId() throws BookNotFoundException {
		when(bookService.getBookById(BigInteger.valueOf(1))).thenThrow(BookNotFoundException.class);
		
		given().config(noSecurity()).
		when().
			get("api/books/id/1").
		then().
			statusCode(404).
			assertThat().
				body(is(equalTo(BOOK_NOT_FOUND))
			);
		
		verify(bookService, times(1)).getBookById(BigInteger.valueOf(1));
		verifyNoMoreInteractions(bookService);
	}
	
	@Test
	public void testGET_getBookById_WithExistingId() throws BookNotFoundException {
		when(bookService.getBookById(BigInteger.valueOf(1))).
				thenReturn(new Book(BigInteger.valueOf(1), "testTitle", "author1", 7.0));
		
		given().config(noSecurity()).
		when().
			get("api/books/id/1").
		then().
			statusCode(200).
			assertThat().
				contentType(MediaType.APPLICATION_JSON_VALUE).
				body("id", equalTo(1),
					 "title", equalTo("testTitle"),
				     "author", equalTo("author1"),
				     "price", equalTo(7.0f)
				);
		
		verify(bookService, times(1)).getBookById(BigInteger.valueOf(1));
		verifyNoMoreInteractions(bookService);
	}
	
	@Test
	public void testGET_getBookByTitle_WithNonExistingTitle() throws BookNotFoundException {
		when(bookService.getBookByTitle(anyString())).thenThrow(BookNotFoundException.class);
		
		given().config(noSecurity()).config(noSecurity()).
		when().
			get("api/books/title/notFound").
		then().
			statusCode(404).
			assertThat().
				body(is(equalTo(BOOK_NOT_FOUND)));
		
		verify(bookService, times(1)).getBookByTitle(anyString());
		verifyNoMoreInteractions(bookService);
	}
	
	@Test
	public void testGET_getBookByTitle_WithExistingTitle() throws BookNotFoundException {
		when(bookService.getBookByTitle(anyString())).
			thenReturn(asList(
					new Book(BigInteger.valueOf(1), "testTitle", "author1", 10.0),
						new Book(BigInteger.valueOf(2), "testTitle", "author2", 15.0)));
		
		given().config(noSecurity()).
		when().
			get("api/books/title/testTitle").
		then().
			statusCode(200).
			assertThat().
			contentType(MediaType.APPLICATION_JSON_VALUE).
				body("id[0]", equalTo(1),
					 "title[0]", equalTo("testTitle"),
					 "author[0]", equalTo("author1"),
					 "price[0]", equalTo(10.0f),
					 "id[1]", equalTo(2),
					 "title[1]", equalTo("testTitle"),
					 "author[1]", equalTo("author2"),
					 "price[1]", equalTo(15.0f)
				);
		
		verify(bookService, times(1)).getBookByTitle("testTitle");
		verifyNoMoreInteractions(bookService);
	}
	
	@Test
	public void testPOST_insertNewBook() {
		Book requesBodyBook = new Book(null, "testTitle", "author1", 7.0);
		when(bookService.insertNewBook(requesBodyBook)).
			thenReturn(new Book(BigInteger.valueOf(1), "testTitle", "author1", 7.0));
		
		given().config(noSecurity()).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(requesBodyBook).
		when().
			post("api/books/new").
		then().
			statusCode(200).
			assertThat().
				body("id", equalTo(1),
					 "title", equalTo("testTitle"),
					 "author", equalTo("author1"),
					 "price", equalTo(7.0f)
				);
		verify(bookService, times(1)).insertNewBook(requesBodyBook);
		verifyNoMoreInteractions(bookService);
	}
	
	@Test
	public void testPUT_editBookById_WithNonExistingId() throws BookNotFoundException {
		Book book = new Book(null, "testTitle", "author1", 0.0);
		when(bookService.editBookById(BigInteger.valueOf(1), book)).thenThrow(BookNotFoundException.class);
		
		given().config(noSecurity()).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(book).
		when().
			put("api/books/edit/1").
		then().
			statusCode(404).
			assertThat().
				body(is(equalTo(BOOK_NOT_FOUND)));
		
		verify(bookService, times(1)).editBookById(BigInteger.valueOf(1), book);	
	}
	
	@Test
	public void testPUT_editBookById_WithExistingId() throws BookNotFoundException {
		Book requestBodyBook = new Book(null, "testTitle", "author1", 10.0);
		when(bookService.editBookById(BigInteger.valueOf(1), requestBodyBook)).
			thenReturn(new Book(BigInteger.valueOf(1), "testTitle", "author1", 10.0));
		
		given().config(noSecurity()).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(requestBodyBook).
		when().
			put("api/books/edit/1").
		then().
			statusCode(200).
			assertThat().
			body("id", equalTo(1),
				 "title", equalTo("testTitle"),
				 "author", equalTo("author1"),
				 "price", equalTo(10.0f)
				);
		
		verify(bookService, times(1)).editBookById(BigInteger.valueOf(1), requestBodyBook);
		verifyNoMoreInteractions(bookService);
	}
	
	@Test
	public void testDELETE_deleteBookById_WithNonExistingId() throws BookNotFoundException {
		when(bookService.getBookById(BigInteger.valueOf(1))).thenThrow(BookNotFoundException.class);
		
		given().config(noSecurity()).
		when().
			delete("api/books/delete/1").
		then().
			statusCode(404).
			assertThat().
				body(is(equalTo(BOOK_NOT_FOUND))
			);
		
		verify(bookService, times(1)).getBookById(BigInteger.valueOf(1));
	}
	
	@Test
	public void testDELETE_deleteBookById() throws BookNotFoundException {
		Book bookToDelete = new Book(BigInteger.valueOf(1), "testTitle", "author1", 10.0);
		when(bookService.getBookById(BigInteger.valueOf(1))).thenReturn(bookToDelete);
		
		given().config(noSecurity()).
		when().
			delete("api/books/delete/1").
		then().
			statusCode(200);
		
		verify(bookService, times(1)).getBookById(BigInteger.valueOf(1));
		verify(bookService, times(1)).deleteOneBook(bookToDelete);
	}
	
	@Test
	public void testDELETE_deleteAllBooks() {
		
		given().config(noSecurity()).
		when().
			delete("api/books/deleteAll").
		then().
			statusCode(200);
		
		verify(bookService, times(1)).deleteAllBooks();
	}

	private RestAssuredMockMvcConfig noSecurity() {
    
		return RestAssuredMockMvcConfig.config().mockMvcConfig(
    		MockMvcConfig.mockMvcConfig().dontAutomaticallyApplySpringSecurityMockMvcConfigurer());
	}
}
