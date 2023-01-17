package com.carp.forum.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
	public void deleteById(long id) {
		boardRepository.deleteById(id);
	}

	@Override
	public List<BoardDto> findAll(int page, int max, String search) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BoardDto update(BoardDto board) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
