package com.carp.forum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carp.forum.dto.PostDto;
import com.carp.forum.service.IPostService;

@RestController
@RequestMapping("/api/post")
public class PostController {
	
	@Autowired
	private IPostService postService;
	
	@PostMapping(consumes="application/json",produces = "application/json")
	public ResponseEntity<PostDto> save(@RequestBody PostDto post){
		
		PostDto result = postService.save(post);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}
	
	@GetMapping(produces = "application/json", value="/{id}")
	public ResponseEntity<PostDto> findById(@PathVariable("id")long id){
		PostDto result = postService.findById(id);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	@DeleteMapping(produces="application/json",value="/{id}")
	public ResponseEntity<Long> deleteById(@PathVariable("id")long id){
		postService.deleteById(id);
		return ResponseEntity.status(HttpStatus.OK).body(id);
	}
	@GetMapping(produces = "application/json")
	public ResponseEntity<List<PostDto>> findAll(){
		List<PostDto> result= postService.findAll(0, 0, null);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
}
