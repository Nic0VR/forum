package com.carp.forum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carp.forum.dto.BoardDto;
import com.carp.forum.service.IBoardService;

@RestController
@RequestMapping("/api/board")
public class BoardController {
	@Autowired
	private IBoardService boardService;
	
	@PostMapping(consumes="application/json",produces = "application/json")
	public ResponseEntity<BoardDto> save(@RequestBody BoardDto board){
		
		BoardDto result = boardService.save(board);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}
	
	@GetMapping(produces = "application/json", value="/{id}")
	public ResponseEntity<BoardDto> findById(@PathVariable("id")long id){
		BoardDto result = boardService.findById(id);
		if(result == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	public ResponseEntity<Long> deleteById(@PathVariable("id")long id){
		Long result = boardService.deleteById(id);
		if(result == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(id);
	}
	
	@GetMapping(value="/page",produces = "application/json")
	public ResponseEntity<List<BoardDto>> findPage(@RequestParam(value="search",defaultValue = "")String search,
			@RequestParam(value="page",defaultValue = "1")int page,
			@RequestParam(value="max",defaultValue = "20")int max){
		
		
		List<BoardDto> result = boardService.findAll(page-1, max, search);
		if(result.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(result) ;
		
	}
}
