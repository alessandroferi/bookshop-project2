package com.feri.alessandro.attsw.project.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.feri.alessandro.attsw.project.exception.EmailExistException;
import com.feri.alessandro.attsw.project.exception.UsernameExistException;
import com.feri.alessandro.attsw.project.model.User;
import com.feri.alessandro.attsw.project.repositories.UserRepository;


@RunWith(MockitoJUnitRunner.class)
public class UserServiceWithMockitoTest {

	@Mock
	UserRepository userRepository;
	
	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@InjectMocks
	UserService userService;
	
	@Test
	public void test_findByEmail() throws EmailExistException {
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		
		assertThat(userService.findUserByEmail(anyString())).isNull();
		
		assertThatCode(() -> userService.findUserByEmail(anyString())).doesNotThrowAnyException();
	
		verify(userRepository, times(2)).findByEmail(anyString());
	}
	
	@Test
	public void test_findByEmail_whenUserAlreadyExist_shouldThrowException() {
		User user = new User("1L", "tested_email@gmail", "username", "password");
		
		when(userRepository.findByEmail("tested_email@gmail")).thenReturn(Optional.of(user));
		
		assertThatThrownBy(() -> 
				userService.findUserByEmail("tested_email@gmail")).
					isInstanceOf(EmailExistException.class);
		
		verify(userRepository).findByEmail("tested_email@gmail");
	}
	
	@Test
	public void test_findByUsername() throws UsernameExistException {
		
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
		
		assertThat(userService.findUserByUsername(anyString())).isNull();
		
		assertThatCode(() -> userService.findUserByUsername(anyString())).doesNotThrowAnyException();
		
		verify(userRepository, times(2)).findByUsername(anyString());
	}
	
	@Test
	public void test_findByUsername_whenUserAlreadyExist_shouldThrowException() {
		User user = new User("1L", "email@gmail", "tested_username", "password");
		
		when(userRepository.findByUsername("tested_username")).thenReturn(Optional.of(user));
		
		assertThatThrownBy(() -> 
				userService.findUserByUsername("tested_username")).
					isInstanceOf(UsernameExistException.class);
		
		verify(userRepository).findByUsername("tested_username");
	}
	
	@Test
	public void test_loadByUsername() {
		User user = new User("1L", "email@gmail", "tested_username", "password");
		
		when(userRepository.findByEmail("email@gmail")).thenReturn(Optional.of(user));
		
		assertThat(userService.loadUserByUsername("email@gmail")).isSameAs(user);
		
		verify(userRepository).findByEmail("email@gmail");
	}
	
	@Test
	public void test_loadByUsername_whenUserNotExist_shouldThrowException() {
		when(userRepository.findByEmail("email@gmail")).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> 
				userService.loadUserByUsername("email@gmail")).
					isInstanceOf(UsernameNotFoundException.class);
		
		verify(userRepository).findByEmail(anyString());
	}
	
	@Test
	public void test_saveUser() {
		User user = new User(null, "email", "username", "password");
		
		when(userRepository.save(isA(User.class))).thenReturn(user);
		when(bCryptPasswordEncoder.encode(user.getPassword())).thenReturn("password_encoded");
		
		assertThat(user.getPassword()).isEqualTo("password");
		
		userService.saveUser(user);
		
		assertThat(user.getPassword()).isEqualTo("password_encoded");
		verify(userRepository).save(isA(User.class));
	}
	
}
