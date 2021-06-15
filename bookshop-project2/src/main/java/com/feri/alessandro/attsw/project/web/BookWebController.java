package com.feri.alessandro.attsw.project.web;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.feri.alessandro.attsw.project.exception.BookNotFoundException;
import com.feri.alessandro.attsw.project.model.Book;
import com.feri.alessandro.attsw.project.model.BookDTO;
import com.feri.alessandro.attsw.project.services.BookService;

@Controller
public class BookWebController {

	private static final String REDIRECT = "redirect:/";

	private static final String MESSAGE = "message";

	private static final String EMPTY_MESSAGE = "";
	
	@Autowired
	private BookService bookService;
	
	@GetMapping("/")
	public String getIndex(Model model) {
		List<Book> allBooks = bookService.getAllBooks();
		model.addAttribute("books", allBooks);
		model.addAttribute(MESSAGE, allBooks.isEmpty() ? "There are no books." : EMPTY_MESSAGE);
		
		return "index";
	}

	@GetMapping("/edit/{id}")
	public String editBookById(@PathVariable BigInteger id, Model model) throws BookNotFoundException {
		Book book = bookService.getBookById(id);
		model.addAttribute("book", book);
		model.addAttribute(MESSAGE, EMPTY_MESSAGE);
			
		return "edit";
	}
	
	@GetMapping("/new")
	public String newBook(Model model) {
		model.addAttribute("book", new Book());
		model.addAttribute(MESSAGE, EMPTY_MESSAGE);
		
		return "edit";
	}
	
	@PostMapping("/save")
	public String saveBook(BookDTO bookDTO) throws BookNotFoundException {
		Book book = new Book(bookDTO.getId(), bookDTO.getTitle(), bookDTO.getAuthor(), bookDTO.getPrice());
		
		BigInteger id = book.getId();
		
		if(id == null) {
			bookService.insertNewBook(book);
		} else {
			bookService.editBookById(id, book);
		}
		
		return REDIRECT;
	}
	
	@GetMapping("/search")
	public String search (@RequestParam("title_searched") String title, Model model) throws BookNotFoundException {
		if(title.equals("")) {
			model.addAttribute(MESSAGE, "Error! Please, insert a valid title.");
		} else {
		
		List<Book> books = bookService.getBookByTitle(title);
		model.addAttribute("books", books);
		model.addAttribute(MESSAGE, EMPTY_MESSAGE);
		
		}
		
		return "search";
	}
	
	@GetMapping("/delete")
	public String deleteBook(@RequestParam("id") BigInteger id) throws BookNotFoundException {
		Book toDelete = bookService.getBookById(id);
		bookService.deleteOneBook(toDelete);
		
		return REDIRECT;
	}
	
	@GetMapping("/deleteAll")
	public String deleteAll() {
		bookService.deleteAllBooks();
	
		return REDIRECT;
		
	}

}
