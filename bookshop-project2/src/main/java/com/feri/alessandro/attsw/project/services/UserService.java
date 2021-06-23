package com.feri.alessandro.attsw.project.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.feri.alessandro.attsw.project.exception.EmailExistException;
import com.feri.alessandro.attsw.project.exception.UsernameExistException;
import com.feri.alessandro.attsw.project.model.User;
import com.feri.alessandro.attsw.project.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {

	private UserRepository userRepository;

	private PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public User findUserByEmail(String email) throws EmailExistException {
		if(email == null) 
			throw new IllegalArgumentException();
		
		if(userRepository.findByEmail(email).isPresent())
			throw new EmailExistException("Email already exist");
		
		return null;
	}

	public User findUserByUsername(String username) throws UsernameExistException {
		if(username == null) 
			throw new IllegalArgumentException();
		
		if(userRepository.findByUsername(username).isPresent())
			throw new UsernameExistException("Username already exist");
		
		return null;
	}

	public void saveUser(User user) {
		if(user == null) 
			throw new IllegalArgumentException();
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if(username == null) 
			throw new IllegalArgumentException();
		
		return userRepository.findByUsername(username).
				orElseThrow(() ->
						new UsernameNotFoundException("user not found"));
	}

}
