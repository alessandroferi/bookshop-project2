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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.feri.alessandro.attsw.project.exception.EmailExistException;
import com.feri.alessandro.attsw.project.exception.UsernameExistException;
import com.feri.alessandro.attsw.project.model.User;
import com.feri.alessandro.attsw.project.repositories.UserRepository;
import com.feri.alessandro.attsw.project.services.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(UserService.class)
public class UserServiceRepositoryIT {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Before
	public void setUp() {
		userRepository.deleteAll();
	}

	
	@Test
	public void test_findUserByEmail() throws EmailExistException {
		assertNull(userService.findUserByEmail("email@gmail"));
	}
	
	@Test
	public void test_findUserByUsername() throws UsernameExistException {
		assertNull(userService.findUserByUsername("username"));
	}
	
	@Test
	public void test_findUserByEmail_whenEmailAlreadyExist() {
		User saved = new User();
		
		saved.setEmail("already_exist@gmail");
		saved.setUsername("username");
		saved.setPassword("password");
		
		userRepository.save(saved);
	
		assertThatThrownBy(() -> 
			userService.findUserByEmail("already_exist@gmail")).
				isInstanceOf(EmailExistException.class).
					hasMessage("Email already exist");		
	}
	
	@Test
	public void test_findUserByUsername_whenUsernameAlreadyExist() {
		User saved = new User();
		
		saved.setEmail("email@gmail");
		saved.setUsername("username");
		saved.setPassword("password");
		
		userRepository.save(saved);
		
		assertThatThrownBy(() ->
				userService.findUserByUsername("username")).
				isInstanceOf(UsernameExistException.class).
					hasMessage("Username already exist");
		
	}
	
	@Test
	public void test_ServiceCanInsertIntoRepository() {
		User toInsert = new User();
		
		toInsert.setEmail("email@gmail");
		toInsert.setUsername("username");
		toInsert.setPassword("password");
		
		userService.saveUser(toInsert);
		
		assertThat(userRepository.findAll().size()).isEqualTo(1);
		assertThat(userRepository.findAll().get(0).getEmail()).isEqualTo("email@gmail");
		assertThat(userRepository.findAll().get(0).getUsername()).isEqualTo("username");
		assertThat(userRepository.findAll().get(0).getPassword()).isNotEqualTo("password");
		assertTrue(bCryptPasswordEncoder.matches("password", userRepository.findAll().get(0).getPassword()));	
	}
	
	@Test
	public void test_loadUserByUsername() {
		User saved = new User();
		
		saved.setEmail("saved@gmail");
		saved.setUsername("saved_username");
		saved.setPassword("saved_passowrd");
		
		userRepository.save(saved);
		
		assertThatCode(() -> userService.loadUserByUsername("saved_username")).doesNotThrowAnyException();
		
		assertThat(userService.loadUserByUsername("saved_username")).isEqualTo(saved);
		
	}
	
	@Test
	public void test_loadUserByUsername_withUserNotFound() {
		User not_saved = new User();
		
		not_saved.setEmail("not_saved@gmail");
		not_saved.setUsername("username_not_saved");
		not_saved.setPassword("not_saved");
		
		assertThatThrownBy(() -> 
		userService.loadUserByUsername("username_not_saved")).
			isInstanceOf(UsernameNotFoundException.class).
				hasMessage("user not found");
		
		assertThat(userRepository.findByUsername("username_not_saved")).isEmpty();
		assertThat(userRepository.findByEmail("not_saved@gmail")).isEmpty();
		
	}	
	
}
