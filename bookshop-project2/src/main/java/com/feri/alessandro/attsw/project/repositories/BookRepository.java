package com.feri.alessandro.attsw.project.repositories;

import java.math.BigInteger;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.feri.alessandro.attsw.project.model.Book;

public interface BookRepository extends MongoRepository<Book, BigInteger>{
		
}
