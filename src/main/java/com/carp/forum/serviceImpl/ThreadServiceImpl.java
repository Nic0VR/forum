package com.carp.forum.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carp.forum.dto.ThreadDto;
import com.carp.forum.entities.Thread;
import com.carp.forum.repository.ThreadRepository;
import com.carp.forum.service.IThreadService;
import com.carp.forum.tools.DtoTools;


@Service
public class ThreadServiceImpl implements IThreadService {

	@Autowired
	private ThreadRepository threadRepository;
	
	@Override
	public ThreadDto save(ThreadDto thread) {
		Thread entityToSave = DtoTools.convert(thread, Thread.class);
		entityToSave = threadRepository.saveAndFlush(entityToSave);
		ThreadDto entitySaved = DtoTools.convert(entityToSave, ThreadDto.class);
		return entitySaved;
	}

	@Override
	public ThreadDto findById(long id) {
		Optional<Thread> resultInDb = threadRepository.findById(id);
		if(resultInDb.isPresent()) {
			ThreadDto result = DtoTools.convert(resultInDb.get(), ThreadDto.class);
			return result;
		}
		return null;
	}

	@Override
	public void deleteById(long id) {
		threadRepository.deleteById(id);
	}

	@Override
	public List<ThreadDto> findAll(int page, int max, String search) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ThreadDto update(ThreadDto thread) {
		// TODO Auto-generated method stub
		return null;
	}

}
