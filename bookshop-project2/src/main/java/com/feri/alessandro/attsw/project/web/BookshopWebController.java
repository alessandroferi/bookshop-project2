package com.feri.alessandro.attsw.project.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.feri.alessandro.attsw.project.exception.BookNotFoundException;
import com.feri.alessandro.attsw.project.exception.EmailExistException;
import com.feri.alessandro.attsw.project.exception.UsernameExistException;
import com.feri.alessandro.attsw.project.model.Book;
import com.feri.alessandro.attsw.project.model.BookDTO;
import com.feri.alessandro.attsw.project.model.User;
import com.feri.alessandro.attsw.project.model.UserDTO;
import com.feri.alessandro.attsw.project.services.BookService;
import com.feri.alessandro.attsw.project.services.UserService;


@Controller
public class BookshopWebController {
	
	private static final String REDIRECT = "redirect:/";

	private static final String MESSAGE = "message";

	private static final String EMPTY_MESSAGE = "";

	@Autowired
	private BookService bookService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/login")
	public ModelAndView getLoginPage() {
		return new ModelAndView("login");
	}
	
	@GetMapping("/registration")
	public ModelAndView getRegistrationPage() {
		return new ModelAndView("registration");
	}
	
	@PostMapping("/saveUser")
	public ModelAndView createNewUser(UserDTO userDTO, Model model) throws EmailExistException, UsernameExistException {
		User user = new User();
		user.setEmail(userDTO.getEmail());
		user.setUsername(userDTO.getUsername());
		user.setPassword(userDTO.getPassword());
		
		userService.findUserByEmail(user.getEmail());
		userService.findUserByUsername(user.getUsername());
		
		model.addAttribute(MESSAGE, EMPTY_MESSAGE);
		userService.saveUser(user);
		
		return new ModelAndView("registrationResult");
	}
	
	
	@GetMapping("/")
	public ModelAndView getIndex(Model model) {
		List<Book> allBooks = bookService.getAllBooks();
		model.addAttribute("books", allBooks);
		model.addAttribute(MESSAGE, allBooks.isEmpty() ? "There are no books." : EMPTY_MESSAGE);
		
		return new ModelAndView("index");
	}

	@GetMapping("/edit/{id}")
	public ModelAndView editBookById(@PathVariable Long id, Model model) throws BookNotFoundException {
		Book book = bookService.getBookById(id);
		model.addAttribute("book", book);
		model.addAttribute(MESSAGE, EMPTY_MESSAGE);
			
		return new ModelAndView("edit");
	}
	
	@GetMapping("/new")
	public ModelAndView newBook(Model model) {
		model.addAttribute("book", new Book());
		model.addAttribute(MESSAGE, EMPTY_MESSAGE);
		
		return new ModelAndView("edit");
	}
	
	@PostMapping("/save")
	public ModelAndView saveBook(BookDTO bookDTO) throws BookNotFoundException {
		Book book = new Book();
		book.setId(bookDTO.getId());
		book.setTitle(bookDTO.getTitle());
		book.setAuthor(bookDTO.getAuthor());
		book.setPrice(bookDTO.getPrice());
		
		Long id = book.getId();
		if(id == null) {
			bookService.insertNewBook(book);
		} else {
			bookService.editBookById(id, book);
		}
		
		return new ModelAndView(REDIRECT);
	}
	
	@GetMapping("/search")
	public ModelAndView search (@RequestParam("title_searched") String title, Model model) throws BookNotFoundException {
		if(title.equals("")) {
			model.addAttribute(MESSAGE, "Error! Please, insert a valid title.");
		} else {
		
		Book book = bookService.getBookByTitle(title);
		model.addAttribute("book", book);
		model.addAttribute(MESSAGE, EMPTY_MESSAGE);
		
		}
		
		return new ModelAndView("search");
	}
	
	@GetMapping("/delete")
	public ModelAndView deleteBook(@RequestParam("id") Long id, Model model) throws BookNotFoundException {
		Book toDelete = bookService.getBookById(id);
		bookService.deleteOneBook(toDelete);
		
		return new ModelAndView(REDIRECT);
	}
	
	@GetMapping("/deleteAll")
	public ModelAndView deleteAll() {
		bookService.deleteAllBooks();
	
		return new ModelAndView(REDIRECT);
		
	}
}
