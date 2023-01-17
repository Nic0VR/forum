package com.carp.forum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carp.forum.dto.UserDto;
import com.carp.forum.service.IUserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	@Autowired
	private IUserService userService;
	
	@PostMapping(consumes="application/json",produces = "application/json")
	public ResponseEntity<UserDto> save(@RequestBody UserDto user){
		
		UserDto result = userService.save(user);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}
	
	@GetMapping(produces = "application/json", value="/{id}")
	public ResponseEntity<UserDto> findById(@PathVariable("id")long id){
		UserDto result = userService.findById(id);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	public ResponseEntity<Long> deleteById(@PathVariable("id")long id){
		userService.deleteById(id);
		return ResponseEntity.status(HttpStatus.OK).body(id);
	}

}
