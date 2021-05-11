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
	private UserRepository userRepository;
	
	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@InjectMocks
	private UserService userService;
	
	@Test
	public void test_findByEmail_whenEmailAddressNotExist() throws EmailExistException {
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		
		assertThat(userService.findUserByEmail(anyString())).isNull();
		
		assertThatCode(() -> userService.findUserByEmail(anyString())).doesNotThrowAnyException();
	
	}
	
	@Test
	public void test_findByEmail_whenEmailAddressAlreadyExist_shouldThrowException() {
		User user = new User(null, "tested_email@gmail", "username", "password");
		
		when(userRepository.findByEmail("tested_email@gmail")).thenReturn(Optional.of(user));
		
		assertThatThrownBy(() -> 
				userService.findUserByEmail("tested_email@gmail")).
					isInstanceOf(EmailExistException.class);
		
		verify(userRepository).findByEmail("tested_email@gmail");
	}
	
	@Test
	public void test_findByUsername_whenUsernameNotExist() throws UsernameExistException {
		
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
		
		assertThat(userService.findUserByUsername(anyString())).isNull();
		
		assertThatCode(() -> userService.findUserByUsername(anyString())).doesNotThrowAnyException();
		
	}
	
	@Test
	public void test_findByUsername_whenUserAlreadyExist_shouldThrowException() {
		User user = new User(null, "email@gmail", "tested_username", "password");
		
		when(userRepository.findByUsername("tested_username")).thenReturn(Optional.of(user));
		
		assertThatThrownBy(() -> 
				userService.findUserByUsername("tested_username")).
					isInstanceOf(UsernameExistException.class);
		
		verify(userRepository).findByUsername("tested_username");
	}
	
	@Test
	public void test_loadByUsername_withExistingUser() {
		User user = new User(null, "email@gmail", "tested_username", "password");
		
		when(userRepository.findByUsername("tested_username")).thenReturn(Optional.of(user));
		
		assertThat(userService.loadUserByUsername("tested_username")).isSameAs(user);
		
		verify(userRepository).findByUsername("tested_username");
	}
	
	@Test
	public void test_loadByUsername_whenUserNotExist_shouldThrowException() {
		when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> 
				userService.loadUserByUsername("username")).
					isInstanceOf(UsernameNotFoundException.class);
		
		verify(userRepository).findByUsername("username");
	}
	
	@Test
	public void test_saveUser() {
		User user = new User(null, "email", "username", "password");
		
		when(userRepository.save(isA(User.class))).thenReturn(user);
		when(bCryptPasswordEncoder.encode(user.getPassword())).thenReturn("password_encoded");
	
		userService.saveUser(user);
		
		assertThat(user.getPassword()).isEqualTo("password_encoded");
		verify(userRepository).save(isA(User.class));
	}
	
}
