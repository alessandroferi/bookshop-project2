package com.feri.alessandro.attsw.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.feri.alessandro.attsw.project.exception.EmailExistException;
import com.feri.alessandro.attsw.project.exception.UsernameExistException;
import com.feri.alessandro.attsw.project.model.User;
import com.feri.alessandro.attsw.project.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
	public User findUserByEmail(String email) throws EmailExistException {
		if(userRepository.findByEmail(email).isPresent())
			throw new EmailExistException("Email already exist");
		
		return null;
	}

	public User findUserByUsername(String username) throws UsernameExistException {
		if(userRepository.findByUsername(username).isPresent())
			throw new UsernameExistException("Username already exist");
		
		return null;
	}

	public void saveUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email).
				orElseThrow(() ->
						new UsernameNotFoundException("user not found"));
	}

}
