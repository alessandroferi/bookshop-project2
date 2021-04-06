package com.feri.alessandro.attsw.project.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.feri.alessandro.attsw.project.exception.BookNotFoundException;
import com.feri.alessandro.attsw.project.model.Book;
import com.feri.alessandro.attsw.project.model.BookDTO;
import com.feri.alessandro.attsw.project.services.BookService;

@RestController
@RequestMapping("/api/books")
public class BookRestController {

	@Autowired
	private BookService bookService;
	
	@GetMapping
	public List<Book> getAllBooks() {
		return bookService.getAllBooks();
	}
	
	@GetMapping("/id/{id}")
	public Book getBookById(@PathVariable Long id) throws BookNotFoundException {
		return bookService.getBookById(id);
	}
	
	@GetMapping("/title/{title}")
	public Book getBookByTitle(@PathVariable String title) throws BookNotFoundException {
		return bookService.getBookByTitle(title);
	}
	
	@PostMapping("/new")
	public Book insertNewBook(@RequestBody BookDTO bookDTO) {
		Book book = new Book();
		book.setTitle(bookDTO.getTitle());
		book.setAuthor(bookDTO.getAuthor());
		book.setPrice(bookDTO.getPrice());
		
		return bookService.insertNewBook(book);
	}
	
	@PutMapping("/edit/{id}")
	public Book editBookById(@PathVariable Long id, @RequestBody BookDTO replacementBookDTO) throws BookNotFoundException {
		Book replacementBook = new Book();
		replacementBook.setTitle(replacementBookDTO.getTitle());
		replacementBook.setAuthor(replacementBookDTO.getAuthor());
		replacementBook.setPrice(replacementBookDTO.getPrice());
			
		return bookService.editBookById(id, replacementBook);
	}
	
	@DeleteMapping("/delete/{id}")
	public void deleteBookById(@PathVariable Long id) throws BookNotFoundException {
		Book toDelete = bookService.getBookById(id);
		bookService.deleteOneBook(toDelete);
	}
	
	@DeleteMapping("/deleteAll")
	public void deleteAllBooks() {
		bookService.deleteAllBooks();
	}
}
