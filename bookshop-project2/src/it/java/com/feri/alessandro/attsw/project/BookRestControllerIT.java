package com.feri.alessandro.attsw.project;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

import static java.util.Arrays.asList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import com.feri.alessandro.attsw.project.model.Book;
import com.feri.alessandro.attsw.project.repositories.BookRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookRestControllerIT {

	@Autowired
	private BookRepository bookRepository;
	
	@LocalServerPort
	private int port;
	
	@Before
	public void setUp() {
		RestAssured.port = port;
		bookRepository.deleteAll();		
	}
	
	
	@Test
	public void test_getAllBooks_WithNotEmptyRepository() {
		bookRepository.saveAll(
				asList(
					new Book(null, "title1", "author1", 10.0), new Book(null, "title2", "author2", 15.0)));
		
		given().
		when().
			get("/api/books").
		then().
			statusCode(200).
				assertThat().
				body("title[0]", equalTo("title1"),
				 "author[0]", equalTo("author1"),
				 "price[0]", equalTo(10.0f),
				 "title[1]", equalTo("title2"),
				 "author[1]", equalTo("author2"),
				 "price[1]", equalTo(15.0f)
				);
		
	}
	
	
	@Test
	public void test_getBookById_WithExistingBookInTheRepository() {
		Book saved = bookRepository.save(new Book(null, "title", "author", 10.0));
		
		Response response = 
				given().
				when().
					get("/api/books/id/" + saved.getId());
		
		Book result = response.getBody().as(Book.class);
		
		assertEquals(saved, bookRepository.findById(result.getId()).get());
	}
	
	
	@Test
	public void test_getBookByTitle_WithMoreBooksInTheRepository() {
		bookRepository.saveAll(asList(
					new Book(null, "title", "author", 10.0), new Book(null, "title", "author2", 15.0),
						new Book(null, "title2", "author3", 20.0)));
		
		given().
		when().
			get("/api/books/title/title").
		then().
			statusCode(200).
			assertThat().
				body("title[0]", equalTo("title"),
				 "author[0]", equalTo("author"),
				 "price[0]", equalTo(10.0f),
				 "title[1]", equalTo("title"),
				 "author[1]", equalTo("author2"),
				 "price[1]", equalTo(15.0f)
				);
		
	}
	
	@Test
	public void test_newBook() {
		Response response = given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(new Book(null, "title", "author", 10.0)).
				when().
					post("/api/books/new");
		
		Book saved = response.getBody().as(Book.class);

		assertThat(bookRepository.findAll()).containsExactly(saved);
		assertEquals(saved, bookRepository.findById(saved.getId()).get());
	}
	
	
	@Test
	public void test_editBook_WithExistingBookInTheRepository() {
		Book toReplace = bookRepository.save(new Book(null, "title", "author", 10.0));
		
		Response response = given().contentType(MediaType.APPLICATION_JSON_VALUE).
					body(new Book(null, "updated_title", "updated_author", 10.0)).
					when().
						put("/api/books/edit/" + toReplace.getId());
		
		Book result = response.getBody().as(Book.class);
		
		assertThat(bookRepository.findAll()).containsExactly(result);
		assertEquals(result, bookRepository.findById(toReplace.getId()).get());
		assertEquals("updated_title", bookRepository.findById(toReplace.getId()).get().getTitle());
		assertEquals("updated_author", bookRepository.findById(toReplace.getId()).get().getAuthor());
		
	}

	
	@Test
	public void test_deleteBookById_WithExistingBookInTheRepository() {
		Book saved = bookRepository.save(new Book(null, "title", "author", 10.0));
		
		given().
		when().
			delete("/api/books/delete/" + saved.getId()).
		then().
			statusCode(200).
				body(is(equalTo("")));
		
		assertThat(bookRepository.findById(saved.getId())).isNotPresent();
	}
	
	@Test
	public void test_deleteAllBooks() {
		Book saved1 = new Book(null, "title1", "author1", 10.0);
		Book saved2 = new Book(null, "title2", "author", 15.0);
		
		bookRepository.saveAll(asList(saved1, saved2));
		
		given().
		when().
			delete("/api/books/deleteAll").
		then().
			statusCode(200).
				body(is(equalTo("")));
		
		assertThat(bookRepository.findAll()).isEmpty();	
		
	}
	
	
}
