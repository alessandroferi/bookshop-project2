package com.feri.alessandro.attsw.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.feri.alessandro.attsw.project.exception.EmailExistException;
import com.feri.alessandro.attsw.project.exception.UsernameExistException;
import com.feri.alessandro.attsw.project.model.User;
import com.feri.alessandro.attsw.project.repositories.UserRepository;
import com.feri.alessandro.attsw.project.security.SecurityConfiguration;
import com.feri.alessandro.attsw.project.services.UserService;

@RunWith(SpringRunner.class)
@DataMongoTest
@Import({UserService.class, SecurityConfiguration.class})
public class UserServiceRepositoryIT {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;
	
	@Before
	public void setUp() {
		userRepository.deleteAll();
	}

	
	@Test
	public void test_findUserByEmail() throws EmailExistException {
		assertNull(userService.findUserByEmail("email@gmail"));

		assertThatCode(() -> userService.findUserByUsername("email@gmail")).doesNotThrowAnyException();
	}
	
	@Test
	public void test_findUserByUsername() throws UsernameExistException {
		assertNull(userService.findUserByUsername("username"));
		
		assertThatCode(() -> userService.findUserByUsername("username")).doesNotThrowAnyException();
	}
	
	@Test
	public void test_findUserByEmail_whenEmailAlreadyExist() {
		User saved = new User(null, "already_exist@gmail", "username", "password");
		
		userRepository.save(saved);
	
		assertThatThrownBy(() -> 
			userService.findUserByEmail("already_exist@gmail")).
				isInstanceOf(EmailExistException.class).
					hasMessage("Email already exist");		
	}
	
	@Test
	public void test_findUserByUsername_whenUsernameAlreadyExist() {
		User saved = new User(null, "email@gmail", "username", "password");
		
		userRepository.save(saved);
		
		assertThatThrownBy(() ->
				userService.findUserByUsername("username")).
				isInstanceOf(UsernameExistException.class).
					hasMessage("Username already exist");
		
	}
	
	@Test
	public void test_ServiceCanInsertIntoRepository() {
		User toInsert = new User(null, "email@gmail", "username", "password");
		
		userService.saveUser(toInsert);
		
		assertThat(userRepository.findAll().size()).isEqualTo(1);
		assertThat(userRepository.findAll().get(0).getEmail()).isEqualTo("email@gmail");
		assertThat(userRepository.findAll().get(0).getUsername()).isEqualTo("username");
		assertThat(userRepository.findAll().get(0).getPassword()).isNotEqualTo("password");
		assertTrue(bCryptPasswordEncoder.matches("password", userRepository.findAll().get(0).getPassword()));	
	}
	
	@Test
	public void test_loadUserByUsername() {
		User saved = new User(null, "saved@gmail", "saved_username", "saved_password");
		
		userRepository.save(saved);
		
		assertThatCode(() -> userService.loadUserByUsername("saved_username")).doesNotThrowAnyException();
		
		assertThat(userService.loadUserByUsername("saved_username")).isEqualTo(saved);
		
	}
	
	@Test
	public void test_loadUserByUsername_withUserNotFound() {
		
		assertThatThrownBy(() -> 
		userService.loadUserByUsername("username_not_saved")).
			isInstanceOf(UsernameNotFoundException.class).
				hasMessage("user not found");
		
		assertThat(userRepository.findByUsername("username_not_saved")).isNotPresent();
		
	}	
	
}
