package com.carp.forum.controller;

import java.util.Arrays;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.carp.forum.dto.LongDto;
import com.carp.forum.dto.PostDto;
import com.carp.forum.exception.BadPayloadException;
import com.carp.forum.exception.EntityNotFoundException;
import com.carp.forum.exception.ForbiddenActionException;
import com.carp.forum.exception.InvalidUpdateException;
import com.carp.forum.exception.TokenException;
import com.carp.forum.service.IPostService;

@RestController
@RequestMapping("/api/post")
public class PostController {
	
	@Autowired
	private IPostService postService;
	
	@PostMapping(consumes="application/json",produces = "application/json")
	public ResponseEntity<PostDto> save(@RequestBody PostDto post) throws TokenException, ForbiddenActionException, EntityNotFoundException, BadPayloadException{
		
		PostDto result = postService.save(post);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}
	
	@PostMapping(consumes="multipart/form-data",produces = "application/json",value = "/files")
	public ResponseEntity<PostDto> saveWithImage(@RequestPart PostDto post, @RequestPart(required = false) MultipartFile[] files) throws TokenException, ForbiddenActionException, EntityNotFoundException, BadPayloadException{
		List<MultipartFile> fileList= Arrays.asList(files).subList(0, Math.max(files.length, 3));;
		PostDto result = postService.saveWithImage(post,fileList);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}
	
	@PutMapping(consumes="application/json",produces = "application/json",value="/{id}")
	public ResponseEntity<PostDto> update(@RequestBody PostDto post,@PathVariable("id")long id) throws InvalidUpdateException, TokenException, ForbiddenActionException, EntityNotFoundException, BadPayloadException{
		if(post.getId()!=id) {
			throw new InvalidUpdateException("param id in url must match param id in body");
		}
		PostDto result = postService.update(post);
		if(result==null) {
			result = postService.save(post);
			return ResponseEntity.status(HttpStatus.CREATED).body(result);
		}
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	@GetMapping(produces = "application/json", value="/{id}")
	public ResponseEntity<PostDto> findById(@PathVariable("id")long id){
		PostDto result = postService.findById(id);
		if(result==null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	@DeleteMapping(produces="application/json",value="/{id}")
	public ResponseEntity<LongDto> deleteById(@PathVariable("id")long id){
		Long result = postService.deleteById(id);
		if(result==null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LongDto(id));
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new LongDto(id));
	}
	
	@GetMapping(produces = "application/json",value="/page")
	public ResponseEntity<List<PostDto>> findPageByThreadId(
			@RequestParam("thread")long threadId,
			@RequestParam(value="page",required=false,defaultValue="1")int page,
			@RequestParam(value = "max",required = false,defaultValue = "20")int max){
		List<PostDto> result= postService.findPageByThreadId(threadId, page-1, max);
		if(result.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	

}
