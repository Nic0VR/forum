package com.carp.forum.service;

import java.util.List;

import com.carp.forum.dto.BoardDto;
import com.carp.forum.entities.Board;

public interface IBoardService {
	
	BoardDto save(BoardDto board);

	BoardDto findById(long id);

	Long deleteById(long id);

	List<BoardDto> findAll(int page, int max, String search);

	BoardDto update(BoardDto board);
}
