package com.carp.forum.service;

import java.util.List;

import com.carp.forum.dto.ThreadDto;

public interface IThreadService {
	ThreadDto save(ThreadDto thread);

	ThreadDto findById(long id);

	void deleteById(long id);

	List<ThreadDto> findAll(int page, int max, String search);

	ThreadDto update(ThreadDto thread);
}
