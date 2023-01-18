package com.carp.forum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carp.forum.dto.LoginDto;
import com.carp.forum.dto.LoginResponseDto;
import com.carp.forum.service.IUserService;

@RestController
@RequestMapping("/api/login")
public class LoginController {
	/**
	 * Injection du service UtilisateurService
	 */
	@Autowired
	private IUserService userService;
	
	 /**
	  * Vérification des identifiants fournis par l'utilisateur
	 * @param loginDto
	 * @return LoginResponseDto
	 * @throws Exception
	 */
	@PostMapping(consumes="application/json", produces="application/json")
	 public LoginResponseDto checkLogin(@RequestBody LoginDto loginDto) throws Exception {
	        return userService.checkLogin(loginDto);
	 }

}
