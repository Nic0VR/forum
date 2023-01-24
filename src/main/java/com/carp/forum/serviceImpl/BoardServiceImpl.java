package com.carp.forum.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.carp.forum.dto.BoardDto;
import com.carp.forum.entities.Board;
import com.carp.forum.repository.BoardRepository;
import com.carp.forum.service.IBoardService;
import com.carp.forum.tools.DtoTools;

@Service
public class BoardServiceImpl implements IBoardService {

	@Autowired
	private BoardRepository boardRepository;

	@Override
	public BoardDto save(BoardDto board) {
		Board entityToSave = DtoTools.convert(board, Board.class);
		entityToSave = boardRepository.saveAndFlush(entityToSave);
		BoardDto entitySaved = DtoTools.convert(entityToSave, BoardDto.class);
		return entitySaved;
	}

	@Override
	public BoardDto findById(long id) {
		Optional<Board> resultInDb = boardRepository.findById(id);
		if(resultInDb.isPresent()) {
			BoardDto result = DtoTools.convert(resultInDb.get(), BoardDto.class);
			return result;
		}
		return null;
	}

	@Override
	public Long deleteById(long id) {
		if(boardRepository.existsById(id)) {
			boardRepository.deleteById(id);
			return id;
		}
		return null;
	}

	@Override
	public List<BoardDto> findAll(int page, int max, String search) {
		List<Board> resultInDb = boardRepository.findAll(search,PageRequest.of(page, max)).get().collect(Collectors.toList());
		List<BoardDto> result = new ArrayList<BoardDto>();
		for (Board board : resultInDb) {
			BoardDto item = DtoTools.convert(board, BoardDto.class);
			result.add(item);
		}
		return result;
	}

	@Override
	public BoardDto update(BoardDto board) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
