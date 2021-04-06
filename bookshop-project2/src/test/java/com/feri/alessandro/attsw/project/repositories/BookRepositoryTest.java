package com.feri.alessandro.attsw.project.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import com.feri.alessandro.attsw.project.model.Book;

@DataJpaTest
@RunWith(SpringRunner.class)
public class BookRepositoryTest {
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private BookRepository bookRepository;

	@Test
	public void testJpaMapping() {
		Book savedBook = entityManager.persistFlushFind(new Book(null, "testBook", "testAuthor", 10.0));
		assertThat(savedBook.getTitle()).isEqualTo("testBook");
		assertThat(savedBook.getAuthor()).isEqualTo("testAuthor");
		assertThat(savedBook.getPrice()).isEqualTo(10);
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getId()).isPositive();

		LoggerFactory.getLogger(BookRepositoryTest.class).info("Saved Book: " + savedBook.toString());
	}

	@Test
	public void test_findAllWithEmptyDatabase() {
		List<Book> books = bookRepository.findAll();
		
		assertThat(books).isEmpty();
	}

	@Test
	public void test_findAllUsingSave() {
		Book bookSaved = bookRepository.save(new Book(null, "testBook", "testAuthor", 10.0));
		
		List<Book> books = bookRepository.findAll();
		
		assertThat(books).containsExactly(bookSaved);
	}
	
	@Test
	public void test_findBookByTitle() {
		Book bookSaved = new Book(null, "testBook", "testAuthor", 10.0);
		
		entityManager.persistFlushFind(bookSaved);
		
		Optional<Book> found = bookRepository.findByTitle("testBook");
		
		assertEquals(found.get(), bookSaved);
	}
	
	@Test
	public void test_findBooksByAuthor() {
		Book testBook1 = new Book(null, "testTitle1", "Author1", 10.0);
		Book testBook2 = new Book(null, "testTitle2", "Author2", 10.0);
		Book testBook3 = new Book(null, "testTitle3", "Author1", 10.0);
		
		entityManager.persistFlushFind(testBook1);
		entityManager.persistFlushFind(testBook2);
		entityManager.persistFlushFind(testBook3);
		
		List<Book> books = bookRepository.findBooksByAuthor("Author1");
		
		assertThat(books).containsExactly(testBook1, testBook3).doesNotContain(testBook2);
	}
	

	@Test
	public void test_findBooksByTitleOrAuthor() {
		Book testBook1 = entityManager.persistFlushFind(new Book(null, "testTitle1", "Author1", 10.0));
		Book testBook2 = entityManager.persistFlushFind(new Book(null, "testTitle2", "Author1", 10.0));
		Book testBook3 = entityManager.persistFlushFind(new Book(null, "testTitle3", "Author2", 10.0));
		Book testBook4 = entityManager.persistFlushFind(new Book(null, "testTitle4", "Author1", 10.0));
		
		List<Book> books = bookRepository.findBooksByTitleOrAuthor("testTitle2", "Author1");
		
		assertThat(books).containsExactly(testBook1, testBook2, testBook4).doesNotContain(testBook3);
	}
	
	@Test
	public void test_findBookByTitleAndPrice() {
		Book testBook1 = new Book(null, "testTitle1", "testAuthor", 10.0);
		Book testBook2 = new Book(null, "testTitle2", "testAuthor", 10.0);
		Book testBook3 = new Book(null, "testTitle1", "testAuthor", 15.0);

		entityManager.persistFlushFind(testBook1);
		entityManager.persistFlushFind(testBook2);
		entityManager.persistFlushFind(testBook3);

		Optional<Book> book = bookRepository.findBookByTitleAndPrice("testTitle1", 10.0);

		assertEquals(book.get(), testBook1);		
	}
	
	@Test
	public void test_findAllBooksWhosePriceIsWithinAnInterval() {
		Book testBook1 = entityManager.persistFlushFind(new Book(null, "testTitle1", "Author1", 35.0));
		Book testBook2 = entityManager.persistFlushFind(new Book(null, "testTitle2", "Author1", 10.0));
		Book testBook3 = entityManager.persistFlushFind(new Book(null, "testTitle3", "Author2", 25.0));
		Book testBook4 = entityManager.persistFlushFind(new Book(null, "testTitle4", "Author1", 40.0));
		
		List<Book> books = bookRepository.findAllBooksWhosePriceIsWithinAnInterval(10.0, 35.0);
		
		assertThat(books).containsExactly(testBook1, testBook2, testBook3).doesNotContain(testBook4);
	}
	
	@Test
	public void test_findAllBooksByAuthorOrderByTitle() {
		Book testBook1 = entityManager.persistFlushFind(new Book(null, "FirstTitle", "Author1", 55.0));
		Book testBook2 = entityManager.persistFlushFind(new Book(null, "FirstTitle", "Author2", 15.0));
		Book testBook3 = entityManager.persistFlushFind(new Book(null, "SecondTitle", "Author1", 26.0));
		Book testBook4 = entityManager.persistFlushFind(new Book(null, "ThirdTitle", "Author1", 30.0));
		
		List<Book> books = bookRepository.findAllBooksByAuthorOrderByTitle("Author1");
		
		assertThat(books).containsExactly(testBook1, testBook3, testBook4).doesNotContain(testBook2);
	}
	
}