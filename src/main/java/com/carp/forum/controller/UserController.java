package com.carp.forum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carp.forum.dto.UserDto;
import com.carp.forum.exception.DuplicateEntryException;
import com.carp.forum.exception.ForbiddenActionException;
import com.carp.forum.exception.InvalidUpdateException;
import com.carp.forum.exception.TokenException;
import com.carp.forum.service.IUserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	@Autowired
	private IUserService userService;

	@PostMapping(consumes = "application/json", produces = "application/json", value = "/create-account")
	public ResponseEntity<UserDto> save(@RequestBody UserDto user)
			throws DuplicateEntryException, TokenException, ForbiddenActionException {

		UserDto result = userService.save(user);

		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@PutMapping(consumes = "application/json", produces = "application/json", value = "/update-account/{id}")
	public ResponseEntity<UserDto> update(@RequestBody UserDto user, @PathVariable("id")long id) throws InvalidUpdateException, TokenException, ForbiddenActionException, DuplicateEntryException {
		if(id != user.getId()) {
			throw new InvalidUpdateException("param id in url must match param id in body");
		}
		UserDto result = userService.update(user);
		if(result == null) {
			result = userService.save(user);
			return ResponseEntity.status(HttpStatus.CREATED).body(result);
		}
		return ResponseEntity.status(HttpStatus.OK).body(result);

	}

	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<UserDto> findById(@PathVariable("id") long id) {
		UserDto result = userService.findById(id);
		if (result == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@DeleteMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Long> deleteById(@PathVariable("id") long id) {
		Long result = userService.deleteById(id);
		if (result == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(id);
	}

	@GetMapping(value = "/page", produces = "application/json")
	public ResponseEntity<List<UserDto>> findAll(
			@RequestParam(value = "search", required = false, defaultValue = "") String search,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "max", required = false, defaultValue = "20") int max) {

		return ResponseEntity.status(HttpStatus.OK).body(userService.findAll(page - 1, max, search));
	}

}
