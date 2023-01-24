package com.carp.forum.serviceImpl;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.carp.forum.dto.ThreadDto;
import com.carp.forum.entities.Thread;
import com.carp.forum.entities.User;
import com.carp.forum.exception.EntityNotFoundException;
import com.carp.forum.exception.ForbiddenActionException;
import com.carp.forum.exception.TokenException;
import com.carp.forum.repository.ThreadRepository;
import com.carp.forum.repository.UserRepository;
import com.carp.forum.service.IThreadService;
import com.carp.forum.tools.DtoTools;
import com.carp.forum.tools.JwtTokenUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class ThreadServiceImpl implements IThreadService {

	@Autowired
	private ThreadRepository threadRepository;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	public ThreadDto save(ThreadDto thread) throws TokenException, ForbiddenActionException, EntityNotFoundException {
		Thread entityToSave = DtoTools.convert(thread, Thread.class);

		if (thread.getUserId() != null) {
			String headerAuth = request.getHeader("Authorization");
			if (headerAuth == null) {
				throw new TokenException("No Authorization header found but post contains userId");
			}
			String token = headerAuth.substring(7);
			Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
			long userId = claims.get("user_id", Long.class);
			String userType = claims.get("user_type", String.class);
			if (userId != thread.getUserId() && !userType.equals("ADMIN")) {
				throw new ForbiddenActionException("Unauthorized");
			}
			Optional<User> optU = userRepository.findById(thread.getUserId());
			if (optU.isEmpty()) {
				throw new EntityNotFoundException("User with id " + thread.getUserId() + " not found");
			}
			entityToSave.setUser(optU.get());
		} else { // try to assign post to logged user if user is logged
			String headerAuth = request.getHeader("Authorization");
			if (headerAuth != null) {
				String token = headerAuth.substring(7);
				Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
				long userId = claims.get("user_id", Long.class);
				Optional<User> optU = userRepository.findById(userId);
				if (optU.isEmpty()) { 
					throw new EntityNotFoundException("User with id " + thread.getUserId() + " not found");
				}
				entityToSave.setUser(optU.get());
			}

		}

		entityToSave = threadRepository.saveAndFlush(entityToSave);
		ThreadDto entitySaved = DtoTools.convert(entityToSave, ThreadDto.class);
		return entitySaved;
	}

	@Override
	public ThreadDto findById(long id) {
		Optional<Thread> resultInDb = threadRepository.findById(id);
		if (resultInDb.isPresent()) {
			ThreadDto result = DtoTools.convert(resultInDb.get(), ThreadDto.class);
			return result;
		}
		return null;
	}

	@Override
	public Long deleteById(long id) {
		if(threadRepository.existsById(id)) {
			threadRepository.deleteById(id);
			return id;
		}
		return null;
	}

	@Override
	public List<ThreadDto> findAll(int page, int max, String search) {
		List<Thread> resultInDb = threadRepository.findPage(search, PageRequest.of(page, max)).get().collect(Collectors.toList());
		List<ThreadDto> result = new ArrayList<>();
		for (Thread thread : resultInDb) {
			result.add(DtoTools.convert(thread, ThreadDto.class));
			
		}
		return result;
	}

	@Override
	public ThreadDto update(ThreadDto thread) {
		Optional<Thread> threadInDb = threadRepository.findById(thread.getId());
		if(threadInDb.isPresent()) {
			Thread threadToSave = DtoTools.convert(thread, Thread.class);
			//threadToSave.set
		}
		return null;
	}

	@Override
	public List<ThreadDto> findPageByBoardId(long boardId, int page, int max,String search) {
		List<Thread> resultInDb = threadRepository.findPageByBoardId(boardId, search, PageRequest.of(page, max)).get().collect(Collectors.toList());
		List<ThreadDto> result = new ArrayList<>();
		for (Thread thread : resultInDb) {
			result.add(DtoTools.convert(thread, ThreadDto.class));
			
		}
		return result;
	}

}
