package com.feri.alessandro.attsw.project;
import static org.assertj.core.api.Assertions.assertThat;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.feri.alessandro.attsw.project.model.Book;
import com.feri.alessandro.attsw.project.repositories.BookRepository;

@RunWith(SpringRunner.class)
@DataMongoTest
public class BookRepositoryIT {
	
	@Autowired
	private BookRepository bookRepository;
	
	@Before
	public void setUp() {
		bookRepository.deleteAll();
	}
	
	@Test
	public void test_findById() {
		Book saved = new Book(null, "title", "author", 10.0);
		
		bookRepository.save(saved);
		
		assertEquals(saved, bookRepository.findById(saved.getId()).get());
	}
	
	@Test
	public void test_findById_not_found() {
		assertThat(bookRepository.findById(BigInteger.valueOf(1))).isNotPresent();
	}
	
	@Test
	public void test_findByTitle_withOneBookInTheRepository() {
		Book saved = new Book(null, "title", "author", 10.0);	
		
		bookRepository.save(saved);
		
		assertEquals(saved, bookRepository.findByTitle(saved.getTitle()).get(0));
	}
	
	@Test
	public void test_findByTitle_withMoreBooksInTheRepository() {		
		Book book1 = new Book(null, "title", "author1", 10.0);
		Book book2 = new Book(null, "title", "author2", 15.0);
		Book book3 = new Book(null, "title2", "author3", 15.0);
		
		List<Book> books = asList(book1, book2, book3);
		
		bookRepository.saveAll(books);
		
		assertThat(bookRepository.findByTitle("title")).containsExactly(book1,book2).doesNotContain(book3);	
	}
	
	@Test
	public void test_findByTitle_not_found() {
		assertThat(bookRepository.findByTitle("title")).isEmpty();
	}
	
}
