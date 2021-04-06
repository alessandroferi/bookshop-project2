package com.feri.alessandro.attsw.project.services;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.feri.alessandro.attsw.project.exception.BookNotFoundException;
import com.feri.alessandro.attsw.project.model.Book;
import com.feri.alessandro.attsw.project.repositories.BookRepository;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceWithMockitoTest {

	private static final String BOOK_NOT_FOUND = "Book not found!";

	@Mock
	BookRepository bookRepository;
	
	@InjectMocks
	BookService bookService;
	
	@Test
	public void test_getAllBooksWithZeroBooks() {
		when(bookRepository.findAll()).thenReturn(Collections.emptyList());
		
		assertThat(bookService.getAllBooks()).isEmpty();
		
		verify(bookRepository, times(1)).findAll();
		verifyNoMoreInteractions(bookRepository);		
	}
	
	@Test
	public void test_getAllBooksWithOneBook() {
		Book book = new Book(1L, "testBook", "testAuthor", 10.0);
		
		when(bookRepository.findAll()).thenReturn(asList(book));
		
		assertThat(bookService.getAllBooks()).containsExactly(book);
		
		verify(bookRepository, times(1)).findAll();
		verifyNoMoreInteractions(bookRepository);
	}
	
	@Test
	public void test_getAllBooksWithMoreThanOneBook() {
		Book book1 = new Book(1L, "testBook1", "testAuthor1", 10.0);
		Book book2 = new Book(2L, "testBook2", "testAuthor2", 20.0);
		
		when(bookRepository.findAll()).thenReturn(asList(book1, book2));
		
		assertThat(bookService.getAllBooks()).containsExactly(book1, book2);
		
		verify(bookRepository, times(1)).findAll();
		verifyNoMoreInteractions(bookRepository);	
	}
	
	@Test
	public void test_getBookById_found() throws BookNotFoundException {
		Book book = new Book(1L, "testBook", "testAuthor", 10.0);
		
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		
		assertThat(bookService.getBookById(1L)).isSameAs(book);
		
		verify(bookRepository, times(1)).findById(1L);
	}
	
	@Test
	public void test_getBookById_notFound_ShouldThrowException() {
		when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> 
			bookService.getBookById(1L)).
				isInstanceOf(BookNotFoundException.class).
					hasMessage(BOOK_NOT_FOUND);
	}
	
	@Test
	public void test_getBookByTitle_found() throws BookNotFoundException {
		Book book = new Book(1L, "tesedtTitle", "author", 10.0);
		
		when(bookRepository.findByTitle("testedTitle")).thenReturn(Optional.of(book));
		
		assertThat(bookService.getBookByTitle("testedTitle")).isSameAs(book);
		
		verify(bookRepository, times(1)).findByTitle("testedTitle");
		
	}
	
	@Test
	public void test_getBookByTitle_notFound_ShouldThrowException() {
		when(bookRepository.findByTitle("testedTitle")).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> 
				bookService.getBookByTitle("testedTitle")).
					isInstanceOf(BookNotFoundException.class).
						hasMessage(BOOK_NOT_FOUND);
	}
	
	
	@Test
	public void test_insertNewBook_setsIdToNull_and_ReturnsSavedBook() {
		Book bookToSave = spy(new Book(2L, "testBookToSave", "testAuthorToSave", 0.0));
		Book bookSaved = new Book(1L, "testBookSaved", "testAuthorSaved", 10.0);
		
		when(bookRepository.save(any(Book.class))).thenReturn(bookSaved);

		Book resultBook = bookService.insertNewBook(bookToSave); 
		
		assertThat(resultBook).isSameAs(bookSaved);
		
		verify(bookRepository, times(1)).save(bookToSave);
		verifyNoMoreInteractions(bookRepository);

		InOrder inOrder = inOrder(bookToSave, bookRepository);
		inOrder.verify(bookToSave).setId(null);
		inOrder.verify(bookRepository).save(bookToSave);
	}
	
	@Test
	public void test_editBookById_setsIdToArgument_and_ShouldReturnsSavedBook() throws BookNotFoundException {
		Book replacementBook = spy(new Book(null, "replacementBook", "replacementAuthor", 5.0));
		Book replacedBook= new Book(1L, "replacedBook", "replacedAuthor", 10.0);
		
		when(bookRepository.save(any(Book.class))).thenReturn(replacedBook);
		when(bookRepository.findById(1L)).thenReturn(Optional.of(replacedBook));
		
		Book resultBook = bookService.editBookById(1L, replacementBook);
		
		assertThat(resultBook).isSameAs(replacedBook);
		
		verify(bookRepository, times(1)).save(replacementBook);
		verify(bookRepository, times(1)).findById(1L);	
		
		InOrder inOrder = inOrder(bookRepository, replacementBook, bookRepository);
		inOrder.verify(bookRepository).findById(1L);
		inOrder.verify(replacementBook).setId(1L);
		inOrder.verify(bookRepository).save(replacementBook);		
	}
	
	@Test
	public void test_editBookById_WhenIdNotFound_ShouldThrowException() {
		Book bookNotFound = new Book(1L, "titleNotFound", "authorNotFound", 0.0);
		
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> 
			bookService.editBookById(1L, bookNotFound)).
				isInstanceOf(BookNotFoundException.class).
					hasMessage(BOOK_NOT_FOUND);
	}
	
	@Test
	public void test_deleteOneBook() {
		Book bookToDelete = new Book(1L, "titleToDelete", "authorToDelete", 10.0);
		
		when(bookRepository.findById(1L)).thenReturn(Optional.of(bookToDelete));
		
		assertThatCode(() -> bookService.deleteOneBook(bookToDelete)).doesNotThrowAnyException();
		
		verify(bookRepository, times(1)).findById(1L);
		verify(bookRepository, times(1)).delete(bookToDelete);
	}
	
	@Test
	public void test_deleteOneBook_WhenItsIdIsNotFound_ShouldThrowException() {
		Book bookNotFound = new Book(1L, "titleNotFound", "authorNoyFound", 0.0);
		
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> 
			bookService.deleteOneBook(bookNotFound)).
				isInstanceOf(BookNotFoundException.class).
					hasMessage(BOOK_NOT_FOUND);
		
		verify(bookRepository, times(1)).findById(1L);
		verifyNoMoreInteractions(bookRepository);
	}
	
	@Test
	public void test_deleteAllBooks() {
		bookService.deleteAllBooks();
		
		verify(bookRepository, times(1)).deleteAll();
		verifyNoMoreInteractions(bookRepository);
	}	
	
}
