package com.feri.alessandro.attsw.project;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.feri.alessandro.attsw.project.model.User;
import com.feri.alessandro.attsw.project.repositories.UserRepository;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UserRepositoryIT {

	@Autowired
	private UserRepository userRepository;
	
	@Before
	public void setUp() {
		userRepository.deleteAll();
	}
	
	
	@Test
	public void test_findByEmail() {
		User saved = new User(null, "email@gmail", "username", "password");
		
		userRepository.save(saved);
		
		assertThat(userRepository.findByEmail(saved.getEmail()).get()).isEqualTo(saved);
	}
	
	@Test
	public void test_findByEmail_not_found() {
		assertThat(userRepository.findByEmail("email@gmail")).isNotPresent();
	}
	
	@Test
	public void test_findByUsername() {
		User saved = new User(null, "email@gmail", "username", "password");
		
		userRepository.save(saved);
		
		assertThat(userRepository.findByUsername(saved.getUsername()).get()).isEqualTo(saved);
	}
	
	@Test
	public void test_findByUsername_not_found() {
		assertThat(userRepository.findByUsername("username")).isNotPresent();
	}
}
