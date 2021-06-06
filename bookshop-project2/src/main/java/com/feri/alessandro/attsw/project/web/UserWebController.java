package com.feri.alessandro.attsw.project.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.feri.alessandro.attsw.project.exception.EmailExistException;
import com.feri.alessandro.attsw.project.exception.UsernameExistException;
import com.feri.alessandro.attsw.project.model.User;
import com.feri.alessandro.attsw.project.model.UserDTO;
import com.feri.alessandro.attsw.project.services.UserService;


@Controller
public class UserWebController {

	private static final String MESSAGE = "message";

	private static final String EMPTY_MESSAGE = "";
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/login")
	public String getLoginPage() {
		return "login";
	}
	
	@GetMapping("/registration")
	public String getRegistrationPage() {
		return "registration";
	}
	
	@PostMapping("/saveUser")
	public String createNewUser(UserDTO userDTO, Model model) throws EmailExistException, UsernameExistException {
		User user = new User();
		user.setEmail(userDTO.getEmail());
		user.setUsername(userDTO.getUsername());
		user.setPassword(userDTO.getPassword());
		
		userService.findUserByEmail(user.getEmail());
		userService.findUserByUsername(user.getUsername());
		
		model.addAttribute(MESSAGE, EMPTY_MESSAGE);
		userService.saveUser(user);
		
		return "registrationResult";
	}
	
}
