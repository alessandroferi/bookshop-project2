package com.feri.alessandro.attsw.project;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

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
		assertThat(bookRepository.findById(saved.getId()).get()).isEqualTo(saved);
	}
	
	@Test
	public void test_findById_not_found() {
		assertThat(bookRepository.findById(BigInteger.valueOf(1))).isNotPresent();
	}
	
	@Test
	public void test_findByTitle() {
		Book saved = new Book(null, "title", "author", 10.0);	
		bookRepository.save(saved);
		assertThat(bookRepository.findByTitle(saved.getTitle()).get()).isEqualTo(saved);
	}
	
	@Test
	public void test_findByTitle_not_found() {
		assertThat(bookRepository.findByTitle("title")).isNotPresent();
	}
	
}
