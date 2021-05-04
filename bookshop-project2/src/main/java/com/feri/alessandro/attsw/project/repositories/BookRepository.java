package com.feri.alessandro.attsw.project.repositories;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.feri.alessandro.attsw.project.model.Book;

public interface BookRepository extends MongoRepository<Book, BigInteger>{

	Optional<Book> findById(BigInteger id);

	Optional<Book> findByTitle(String title);
		
}
