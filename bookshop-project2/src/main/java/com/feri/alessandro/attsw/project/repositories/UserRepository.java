package com.feri.alessandro.attsw.project.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.feri.alessandro.attsw.project.model.User;

public interface UserRepository extends MongoRepository<User, String>{

	Optional<User> findByEmail(String email);

	Optional<User> findByUsername(String username);

}
