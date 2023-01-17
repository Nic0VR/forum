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

import com.carp.forum.dto.ThreadDto;
import com.carp.forum.service.IThreadService;
@RestController
@RequestMapping("/api/thread")
public class ThreadController {
	@Autowired
	private IThreadService threadService;
	
	@PostMapping(consumes="application/json",produces = "application/json")
	public ResponseEntity<ThreadDto> save(@RequestBody ThreadDto thread){
		
		ThreadDto result = threadService.save(thread);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}
	
	@GetMapping(produces = "application/json", value="/{id}")
	public ResponseEntity<ThreadDto> findById(@PathVariable("id")long id){
		ThreadDto result = threadService.findById(id);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	public ResponseEntity<Long> deleteById(@PathVariable("id")long id){
		threadService.deleteById(id);
		return ResponseEntity.status(HttpStatus.OK).body(id);
	}
}
