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

import com.carp.forum.dto.CountDto;
import com.carp.forum.dto.LongDto;
import com.carp.forum.dto.ThreadDto;
import com.carp.forum.exception.BadPayloadException;
import com.carp.forum.exception.EntityNotFoundException;
import com.carp.forum.exception.ForbiddenActionException;
import com.carp.forum.exception.InvalidUpdateException;
import com.carp.forum.exception.TokenException;
import com.carp.forum.service.IPostService;
import com.carp.forum.service.IThreadService;
@RestController
@RequestMapping("/api/thread")
public class ThreadController {
	@Autowired
	private IThreadService threadService;
	@Autowired
	private IPostService postService;
	

	
	@PostMapping(consumes="application/json",produces = "application/json")
	public ResponseEntity<ThreadDto> save(@RequestBody ThreadDto thread) throws TokenException, ForbiddenActionException, EntityNotFoundException{
		
		ThreadDto result = threadService.save(thread);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}
	
	@PostMapping(consumes="multipart/form-data",produces = "application/json",value = "/files")
	public ResponseEntity<ThreadDto> saveWithImage(@RequestPart ThreadDto thread, @RequestPart(required = false) MultipartFile[] files) throws TokenException, ForbiddenActionException, EntityNotFoundException, BadPayloadException{
		List<MultipartFile> fileList= Arrays.asList(files).subList(0, Math.min(files.length, 3)); // take 3 files max
		ThreadDto result = threadService.saveWithImage(thread,fileList);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}
	
	@GetMapping(produces = "application/json", value="/{id}")
	public ResponseEntity<ThreadDto> findById(@PathVariable("id")long id){
		ThreadDto result = threadService.findById(id);
		if(result == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	@GetMapping(produces = "application/json", value="/{boardId}/count")
	public ResponseEntity<CountDto> countByBoardId(@PathVariable("boardId")long boardId){
		int result= threadService.countThreadsByBoardId(boardId);
		return ResponseEntity.status(HttpStatus.OK).body(new CountDto(result));
		
		
	}
	
	
	@DeleteMapping(produces = "application/json", value="/{id}")
	public ResponseEntity<LongDto> deleteById(@PathVariable("id")long id){
		
		Long result = threadService.deleteById(id);
		if(result == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LongDto(id));
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new LongDto(id));
	}
	
	@GetMapping(produces = "application/json", value="/page")
	public ResponseEntity<List<ThreadDto>> findPageByBoardId(@RequestParam("board")long boardId,
			@RequestParam(value="search",defaultValue="")String search,
			@RequestParam(value="page",defaultValue = "1")int page, 
			@RequestParam(value = "max",defaultValue = "10")int max){
		
		List<ThreadDto> result = threadService.findPageByBoardId(boardId, page-1, max, search);
		if(result.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	
	
	@PutMapping(consumes="application/json",produces = "application/json",value="/{id}")
	public ResponseEntity<ThreadDto> update(@RequestBody ThreadDto thread,@PathVariable("id")long id) throws InvalidUpdateException, TokenException, ForbiddenActionException, EntityNotFoundException, BadPayloadException{
		if(thread.getId()!=id) {
			throw new InvalidUpdateException("param id in url must match param id in body");
		}
		ThreadDto result = threadService.update(thread);
		if(result==null) {
			result = threadService.save(thread);
			return ResponseEntity.status(HttpStatus.CREATED).body(result);
		}
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
}
