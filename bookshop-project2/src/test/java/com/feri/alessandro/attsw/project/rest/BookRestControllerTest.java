package com.feri.alessandro.attsw.project.rest;

import static java.util.Arrays.asList;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.anyLong;


import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.feri.alessandro.attsw.project.exception.BookshopRestExceptionHandler;
import com.feri.alessandro.attsw.project.exception.BookNotFoundException;
import com.feri.alessandro.attsw.project.model.Book;
import com.feri.alessandro.attsw.project.services.BookService;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

@RunWith(MockitoJUnitRunner.class)
public class BookRestControllerTest {

	private static final String BOOK_NOT_FOUND = "Book not found!";

	@Mock
	private BookService bookService;
	
	@InjectMocks
	private BookRestController bookRestController;
	
	/**
	 * Initializes BookshopExceptionHanlder advice using the StaticApplicationContext with the single bean
	 * 
	 * @return HandlerExceptionResolver instantiated based on the BookshopExceptionHanlder
	 * 
	 * So, my BookshopExceptionHanlder is initialized using StaticApplicationContext and then I retrieve
	 * handlerExceptionResolver from it and pass it into RestAssuredMockMvc standaloneSetup()
	 */
	private HandlerExceptionResolver initBookExceptionHandlerResolvers() {
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.registerSingleton("exceptionHandler", BookshopRestExceptionHandler.class);
		
		WebMvcConfigurationSupport webMvcConfigurationSupport = new WebMvcConfigurationSupport();
		webMvcConfigurationSupport.setApplicationContext(applicationContext);
		
		return webMvcConfigurationSupport.handlerExceptionResolver();
	}
	
	@Before
	public void setUp() {
		HandlerExceptionResolver handlerExceptionResolver = initBookExceptionHandlerResolvers();
		
		RestAssuredMockMvc.standaloneSetup(
				MockMvcBuilders
					.standaloneSetup(bookRestController)
					.setHandlerExceptionResolvers(handlerExceptionResolver)
		);
	}
	
	@Test
	public void testGET_allBooksEmpty() {
		when(bookService.getAllBooks()).thenReturn(Collections.emptyList());
		
		given().
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
		Book testBook1 = new Book(1L, "firstTitle", "author1", 7.0);
		Book testBook2 = new Book(2L, "secondTitle", "author2", 9.0);
		when(bookService.getAllBooks()).thenReturn(asList(testBook1, testBook2));
		
		given().
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
		when(bookService.getBookById(anyLong())).thenThrow(BookNotFoundException.class);
		
		given().
		when().
			get("api/books/id/1").
		then().
			statusCode(404).
			assertThat().
				body(is(equalTo(BOOK_NOT_FOUND))
			);
		
		verify(bookService, times(1)).getBookById(anyLong());
		verifyNoMoreInteractions(bookService);
	}
	
	@Test
	public void testGET_getBookById_WithExistingId() throws BookNotFoundException {
		when(bookService.getBookById(anyLong())).
				thenReturn(new Book(1L, "testTitle", "author1", 7.0));
		
		given().
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
		
		verify(bookService, times(1)).getBookById(1L);
		verifyNoMoreInteractions(bookService);
	}
	
	@Test
	public void test_getBookByTitle_WithNonExistingTitle() throws BookNotFoundException {
		when(bookService.getBookByTitle(anyString())).thenThrow(BookNotFoundException.class);
		
		given().
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
	public void test_getBookByTitle_WithExistingTitle() throws BookNotFoundException {
		when(bookService.getBookByTitle(anyString())).
			thenReturn(new Book(1L, "testTitle", "author1", 10.0));
		
		given().
		when().
			get("api/books/title/testTitle").
		then().
			statusCode(200).
			assertThat().
			contentType(MediaType.APPLICATION_JSON_VALUE).
				body("id", equalTo(1),
					 "title", equalTo("testTitle"),
					 "author", equalTo("author1"),
					 "price", equalTo(10.0f)
				);
		
		verify(bookService, times(1)).getBookByTitle("testTitle");
		verifyNoMoreInteractions(bookService);
	}
	
	@Test
	public void testPOST_insertNewBook() {
		Book requesBodyBook = new Book(null, "testTitle", "author1", 7.0);
		when(bookService.insertNewBook(requesBodyBook)).
			thenReturn(new Book(1L, "testTitle", "author1", 7.0));
		
		given().
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
		when(bookService.editBookById(1L, book)).thenThrow(BookNotFoundException.class);
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(book).
		when().
			put("api/books/edit/1").
		then().
			statusCode(404).
			assertThat().
				body(is(equalTo(BOOK_NOT_FOUND)));
		
		verify(bookService, times(1)).editBookById(1L, book);	
	}
	
	@Test
	public void testPUT_editBookById_WithExistingId() throws BookNotFoundException {
		Book requestBodyBook = new Book(null, "testTitle", "author1", 10.0);
		when(bookService.editBookById(1L, requestBodyBook)).
			thenReturn(new Book(1L, "testTitle", "author1", 10.0));
		
		given().
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
		
		verify(bookService, times(1)).editBookById(1L, requestBodyBook);
		verifyNoMoreInteractions(bookService);
	}
	
	@Test
	public void testDELETE_deleteBookById_WithNonExistingId() throws BookNotFoundException {
		when(bookService.getBookById(anyLong())).thenThrow(BookNotFoundException.class);
		
		given().
		when().
			delete("api/books/delete/1").
		then().
			statusCode(404).
			assertThat().
				body(is(equalTo(BOOK_NOT_FOUND))
			);
		
		verify(bookService, times(1)).getBookById(1L);
	}
	
	@Test
	public void testDELETE_deleteBookById() throws BookNotFoundException {
		Book bookToDelete = new Book(1L, "testTitle", "author1", 10.0);
		when(bookService.getBookById(1L)).thenReturn(bookToDelete);
		
		given().
		when().
			delete("api/books/delete/1").
		then().
			statusCode(200);
		
		verify(bookService, times(1)).getBookById(1L);
		verify(bookService, times(1)).deleteOneBook(bookToDelete);
	}
	
	@Test
	public void testDELETE_deleteAllBooks() {
		
		given().
		when().
			delete("api/books/deleteAll").
		then().
			statusCode(200);
		
		verify(bookService, times(1)).deleteAllBooks();
	}

}
