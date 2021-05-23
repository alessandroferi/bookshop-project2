package com.feri.alessandro.attsw.project.services;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feri.alessandro.attsw.project.exception.BookNotFoundException;
import com.feri.alessandro.attsw.project.model.Book;
import com.feri.alessandro.attsw.project.repositories.BookRepository;

@Service
public class BookService {

	private static final String BOOK_NOT_FOUND = "Book not found!";
	
	@Autowired
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

	public List<Book> getBookByTitle(String title) throws BookNotFoundException {
		if (title == null) 
			throw new IllegalArgumentException();
		
		List<Book> result = bookRepository.findByTitle(title);
		
		if (!result.isEmpty())
			return result;
		
		else {
			throw new BookNotFoundException(BOOK_NOT_FOUND);
		}
		
	}
	
	public Book insertNewBook(Book book) {
		if (book == null)
			throw new IllegalArgumentException();
		
		book.setId(null);
		return bookRepository.save(book);
	}
	
	public Book editBookById(BigInteger id, Book replacementBook) throws BookNotFoundException {
		if(replacementBook == null)
			throw new IllegalArgumentException();
		
		sanityCheck(id);
		replacementBook.setId(id);
		return bookRepository.save(replacementBook);
	}	

	public void deleteOneBook(Book book) throws BookNotFoundException {
		if (book == null) 
			throw new IllegalArgumentException();
		
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
