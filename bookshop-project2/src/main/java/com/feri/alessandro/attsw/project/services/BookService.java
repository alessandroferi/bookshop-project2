package com.feri.alessandro.attsw.project.services;

import java.math.BigInteger;
import java.util.List;

import org.springframework.stereotype.Service;

import com.feri.alessandro.attsw.project.exception.BookNotFoundException;
import com.feri.alessandro.attsw.project.model.Book;
import com.feri.alessandro.attsw.project.repositories.BookRepository;

@Service
public class BookService {

	private static final String BOOK_NOT_FOUND = "Book not found!";
	
	private BookRepository bookRepository;

	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}
	
	public Book getBookById(BigInteger id) throws BookNotFoundException {
		if (id == null) 
			throw new IllegalArgumentException();
		
		return bookRepository.findById(id).
				orElseThrow(() -> new BookNotFoundException(BOOK_NOT_FOUND));
	}

	public Book getBookByTitle(String title) throws BookNotFoundException {
		return bookRepository.findByTitle(title).
				orElseThrow(() -> new BookNotFoundException(BOOK_NOT_FOUND));
	}
	
	public Book insertNewBook(Book book) {
		book.setId(null);
		return bookRepository.save(book);
	}
	
	public Book editBookById(BigInteger id, Book replacementBook) throws BookNotFoundException {
		sanityCheck(id);
		replacementBook.setId(id);
		return bookRepository.save(replacementBook);
	}	

	public void deleteOneBook(Book book) throws BookNotFoundException {
		sanityCheck(book.getId());
		bookRepository.delete(book);
	}

	public void deleteAllBooks() {
		bookRepository.deleteAll();
		
	}
	
	private void sanityCheck(BigInteger id) throws BookNotFoundException {
		getBookById(id);
	}

}
