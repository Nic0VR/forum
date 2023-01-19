package com.carp.forum.service;

import java.util.List;

import com.carp.forum.dto.ThreadDto;
import com.carp.forum.exception.EntityNotFoundException;
import com.carp.forum.exception.ForbiddenActionException;
import com.carp.forum.exception.TokenException;

public interface IThreadService {
	ThreadDto save(ThreadDto thread) throws TokenException, ForbiddenActionException, EntityNotFoundException;

	ThreadDto findById(long id);

	Long deleteById(long id);

	List<ThreadDto> findAll(int page, int max, String search);
	
	List<ThreadDto> findPageByBoardId(long boardId,int page,int max,String search);
	
	ThreadDto update(ThreadDto thread);
}
