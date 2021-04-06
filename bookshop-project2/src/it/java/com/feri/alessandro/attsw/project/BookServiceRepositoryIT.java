package com.feri.alessandro.attsw.project;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.feri.alessandro.attsw.project.exception.BookNotFoundException;
import com.feri.alessandro.attsw.project.model.Book;
import com.feri.alessandro.attsw.project.repositories.BookRepository;
import com.feri.alessandro.attsw.project.services.BookService;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(BookService.class)
public class BookServiceRepositoryIT {
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private BookRepository bookRepository;
	
	@Test
	public void test_serviceCanInsertIntoRepository() {
		Book saved = bookService.insertNewBook(
				new Book(null, "testing_title", "testing_author", 10.0));
		
		assertThat(bookRepository.findById(saved.getId())).isPresent();
	}

	@Test
	public void test_serviceCanUpdateIntoRepository() throws BookNotFoundException {
		Book saved = bookRepository.save(new Book(null, "title", "author", 10.0));
		
		Book updated = new Book(saved.getId(), "updated_title", "updated_author", 15.0);
		
		Book result = bookService.editBookById(saved.getId(), updated);
		
		assertThat(bookRepository.findById(saved.getId()).get()).isSameAs(result);
	}
	
	@Test
	public void test_serviceCanDeleteFromRepository() throws BookNotFoundException {
		Book saved = bookRepository.save(new Book(null, "title", "author", 10.0));
		
		assertThat(bookRepository.findById(saved.getId())).isPresent();
		
		bookService.deleteOneBook(saved);
		
		assertThat(bookRepository.findById(saved.getId())).isNotPresent();
	}
	
}
