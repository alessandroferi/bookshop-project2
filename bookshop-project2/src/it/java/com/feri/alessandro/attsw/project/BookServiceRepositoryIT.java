package com.feri.alessandro.attsw.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static java.util.Arrays.asList;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.feri.alessandro.attsw.project.exception.BookNotFoundException;
import com.feri.alessandro.attsw.project.model.Book;
import com.feri.alessandro.attsw.project.repositories.BookRepository;
import com.feri.alessandro.attsw.project.services.BookService;

@RunWith(SpringRunner.class)
@DataMongoTest
@Import(BookService.class)
public class BookServiceRepositoryIT {
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private BookRepository bookRepository;
	
	@Before
	public void setUp() {
		bookRepository.deleteAll();
	}
	
	@Test
	public void test_serviceCanInsertIntoRepository() {
		Book saved = bookService.insertNewBook(
				new Book(null, "testing_title", "testing_author", 10.0));
		
		assertThat(bookRepository.findById(saved.getId())).isPresent();
	}

	@Test
	public void test_serviceCanRetrieveFromRepositoryUsingId() throws BookNotFoundException {
		Book saved = bookRepository.save(new Book(null, "title", "author", 10.0));
		
		Book result = bookService.getBookById(saved.getId());
		
		assertEquals(bookRepository.findById(saved.getId()).get(), result);
		
	}

	@Test
	public void test_serviceCanRetrieveFromRepositoryUsingTitle() throws BookNotFoundException {
		String title = "title";
		
		bookRepository.saveAll(
				asList(new Book(null, "title", "author1", 10.0), new Book(null, "title", "author2", 15.0),
						new Book(null, "different_title", "author", 20.0)));
		
		List<Book> result = bookService.getBookByTitle(title);
		
		assertThat(bookRepository.findByTitle(title)).isEqualTo(result);
		assertThat(result.size()).isEqualTo(2).isEqualTo(bookRepository.findByTitle(title).size());
		
	}
	
	@Test
	public void test_serviceCanUpdateIntoRepository() throws BookNotFoundException {
		Book saved = bookRepository.save(new Book(null, "title", "author", 10.0));
		
		Book updated = new Book(saved.getId(), "updated_title", "updated_author", 15.0);
		
		Book result = bookService.editBookById(saved.getId(), updated);
		
		assertEquals(result, bookRepository.findById(saved.getId()).get());
	}
	
	@Test
	public void test_serviceCanDeleteFromRepository() throws BookNotFoundException {
		Book saved = bookRepository.save(new Book(null, "title", "author", 10.0));
		
		assertThat(bookRepository.findById(saved.getId())).isPresent();
		
		bookService.deleteOneBook(saved);
		
		assertThat(bookRepository.findById(saved.getId())).isNotPresent();
	}
	
}
