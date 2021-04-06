package com.feri.alessandro.attsw.project.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.feri.alessandro.attsw.project.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

	Optional<Book> findByTitle(String title);
	
	@Query("Select b from Book b where b.author = :author")
	List<Book> findBooksByAuthor(@Param("author") String author);
	
	@Query("Select b from Book b where b.title = :title or b.author = :author")
	List<Book> findBooksByTitleOrAuthor(@Param("title") String title, @Param("author") String author);

	@Query("Select b from Book b where b.title = :title and b.price = :price")
	Optional<Book> findBookByTitleAndPrice(@Param("title") String title, @Param("price") double price);

	@Query("Select b from Book b where b.price >= :min and b.price <= :max")
	List<Book> findAllBooksWhosePriceIsWithinAnInterval(@Param("min") double min, @Param("max") double max);
	
	@Query("Select b from Book b where b.author = :author order by b.title")
	List<Book> findAllBooksByAuthorOrderByTitle(@Param("author") String author);
	
}
