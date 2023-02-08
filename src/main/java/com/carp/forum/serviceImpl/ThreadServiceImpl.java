package com.carp.forum.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.carp.forum.dto.FileInfoDto;
import com.carp.forum.dto.ThreadDto;
import com.carp.forum.entities.FileInfo;
import com.carp.forum.entities.Thread;
import com.carp.forum.entities.User;
import com.carp.forum.exception.EntityNotFoundException;
import com.carp.forum.exception.ForbiddenActionException;
import com.carp.forum.exception.TokenException;
import com.carp.forum.exception.UnsupportedFileTypeException;
import com.carp.forum.repository.FileInfoRepository;
import com.carp.forum.repository.ThreadRepository;
import com.carp.forum.repository.UserRepository;
import com.carp.forum.service.FileService;
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
	@Autowired
	private FileService fileService;
	@Autowired
	private FileInfoRepository fileInfoRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadServiceImpl.class);

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
	public ThreadDto saveWithImage(ThreadDto thread, List<MultipartFile> files)
			throws TokenException, ForbiddenActionException, EntityNotFoundException {
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
		List<FileInfoDto> fileList = new ArrayList<>();
		// Files
		for (MultipartFile multipartFile : files) {
			try {
				fileList.add(DtoTools.convert(fileService.saveFile(multipartFile, entityToSave), FileInfoDto.class));
			} catch (IOException | UnsupportedFileTypeException e) {
				LOGGER.info(e.getMessage());
			}
		}
		entitySaved.setFiles(fileList);
		return entitySaved;
	}

	@Override
	public ThreadDto findById(long id) {
		Optional<Thread> resultInDb = threadRepository.findById(id);
		if (resultInDb.isPresent()) {
			ThreadDto result = DtoTools.convert(resultInDb.get(), ThreadDto.class);
			List<FileInfo> fileInfos = fileInfoRepository.findFileInfoByThreadId(result.getId());
			List<FileInfoDto> fileInfodtos = new ArrayList<>();
			for (FileInfo fileInfo : fileInfos) {
				fileInfodtos.add(DtoTools.convert(fileInfo, FileInfoDto.class));
			}
			result.setFiles(fileInfodtos);
			return result;
		}
		return null;
	}

	@Override
	public Long deleteById(long id) {
		if (threadRepository.existsById(id)) {
			List<FileInfo> fileInfos = fileInfoRepository.findFileInfoByThreadId(id);
			for (FileInfo fileInfo : fileInfos) {
				fileService.delete(fileInfo.getFileName());
			}
			fileInfoRepository.deleteByThreadId(id);
			threadRepository.deleteById(id);
			return id;
		}
		return null;
	}

	@Override
	public List<ThreadDto> findAll(int page, int max, String search) {
		List<Thread> resultInDb = threadRepository.findPage(search, PageRequest.of(page, max)).get()
				.collect(Collectors.toList());
		List<ThreadDto> result = new ArrayList<>();
		for (Thread thread : resultInDb) {
			ThreadDto t = DtoTools.convert(thread, ThreadDto.class);

			List<FileInfo> fileInfos = fileInfoRepository.findFileInfoByThreadId(t.getId());
			List<FileInfoDto> fileInfodtos = new ArrayList<>();
			for (FileInfo fileInfo : fileInfos) {
				fileInfodtos.add(DtoTools.convert(fileInfo, FileInfoDto.class));
			}
			t.setFiles(fileInfodtos);

			result.add(t);

		}
		return result;
	}

	// TODO
	@Override
	public ThreadDto update(ThreadDto thread) throws TokenException, ForbiddenActionException, EntityNotFoundException {
		String headerAuth = request.getHeader("Authorization");
		if (headerAuth == null) {
			throw new TokenException("No Authorization header");
		}

		String token = headerAuth.substring(7);
		Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
		long userId = claims.get("user_id", Long.class);
		String userType = claims.get("user_type", String.class);

		Optional<Thread> threadInDb = threadRepository.findById(thread.getId());

		if (threadInDb.isPresent()) {
			// check if user making update request on thread is the same whom created thread
			if (!userType.equals("ADMIN")
					&& (threadInDb.get().getUser() == null || threadInDb.get().getUser().getId() != userId  ||  (thread.getUserId()!=null && thread.getUserId()!=userId))) {
				throw new ForbiddenActionException();
			}
			Thread threadToSave = DtoTools.convert(thread, Thread.class);

			if (userType.equals("ADMIN")) {

				if (thread.getUserId() != null) {
					Optional<User> optU = userRepository.findById(thread.getUserId());
					if (optU.isEmpty()) {
						throw new EntityNotFoundException("User with id " + thread.getUserId() + " not found");
					}
					threadToSave.setUser(optU.get());
				} else {
					if (threadInDb.get().getUser() != null) {
						threadToSave.setUser(threadInDb.get().getUser());
					} else {
						threadToSave.setUser(null);
					}
				}
			} else {
			
				threadToSave.setUser(threadInDb.get().getUser());
			}
			
			
			threadToSave.setCreationDate(threadInDb.get().getCreationDate());
			threadToSave = threadRepository.saveAndFlush(threadToSave);
			return DtoTools.convert(threadToSave, ThreadDto.class);
		}
		return null;
	}

	@Override
	public List<ThreadDto> findPageByBoardId(long boardId, int page, int max, String search) {
		List<Thread> resultInDb = threadRepository.findPageByBoardId(boardId, search, PageRequest.of(page, max)).get()
				.collect(Collectors.toList());
		List<ThreadDto> result = new ArrayList<>();
		for (Thread thread : resultInDb) {
			ThreadDto t = DtoTools.convert(thread, ThreadDto.class);

			List<FileInfo> fileInfos = fileInfoRepository.findFileInfoByThreadId(t.getId());
			List<FileInfoDto> fileInfodtos = new ArrayList<>();
			for (FileInfo fileInfo : fileInfos) {
				fileInfodtos.add(DtoTools.convert(fileInfo, FileInfoDto.class));
			}
			t.setFiles(fileInfodtos);
			result.add(t);
		}
		return result;
	}

	@Override
	public int countThreadsByBoardId(long boardId) {
		return threadRepository.countByBoardId(boardId);
		
	}

}
