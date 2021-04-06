package com.feri.alessandro.attsw.project;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static java.util.Arrays.asList;

import java.util.List;

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
		bookRepository.flush();
	}
	
	
	@Test
	public void test_getAllBooksFromNotEmptyRepository() {
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
	public void test_getBookByIdWithExistingBookInTheRepository() {
		Book saved = bookRepository.save(new Book(null, "title", "author", 10.0));
		
		Response response = 
				given().
				when().
					get("/api/books/id/" + saved.getId());
		
		Book result = response.getBody().as(Book.class);
		
		assertThat(bookRepository.findById(result.getId()).get()).isEqualTo(saved);
	}
	
	
	@Test
	public void test_getBookByTitleWithExistingBookInTheRepository() {
		Book saved = bookRepository.save(new Book(null, "title", "author", 10.0));
		
		Response response = 
				given().
				when().
					get("/api/books/title/" + saved.getTitle());
		
		Book result = response.getBody().as(Book.class);
		
		assertThat(bookRepository.findByTitle(result.getTitle()).get()).isEqualTo(saved);
	}
	
	@Test
	public void test_NewBook() {
		Response response = given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(new Book(null, "title", "author", 10.0)).
				when().
					post("/api/books/new");
		
		Book saved = response.getBody().as(Book.class);
		
		assertThat(bookRepository.findById(saved.getId()).get()).isEqualTo(saved);
	}
	
	
	@Test
	public void test_EditBookWithExistingBookInTheRepository() {
		Book replacedBook = bookRepository.save(new Book(null, "title", "author", 10.0));
		
		Response response = given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(new Book(null, "updated_title", "updated_author", 10.0)).
				when().
					put("/api/books/edit/" + replacedBook.getId());
		
		Book editedBook = response.getBody().as(Book.class);
		
		assertThat(bookRepository.findById(editedBook.getId()).get()).isEqualTo(editedBook);		
	}

	
	@Test
	public void test_deleteBookByIdWithExistingBookInTheRepository() {
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
		List<Book> books = bookRepository.saveAll(
				asList(
					new Book(null, "title1", "author1", 10.0), new Book(null, "title2", "author", 15.0)));
		
		assertThat(bookRepository.findAll()).isEqualTo(books);
		
		given().
		when().
			delete("/api/books/deleteAll").
		then().
			statusCode(200).
				body(is(equalTo("")));
		
		assertThat(bookRepository.findAll()).isEmpty();	
		
	}
	
	
}
