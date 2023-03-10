package com.carp.forum.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.carp.forum.controller.FileController;
import com.carp.forum.dto.FileInfoDto;
import com.carp.forum.dto.PostDto;
import com.carp.forum.entities.FileInfo;
import com.carp.forum.entities.Post;
import com.carp.forum.entities.User;
import com.carp.forum.exception.EntityNotFoundException;
import com.carp.forum.exception.ForbiddenActionException;
import com.carp.forum.exception.TokenException;
import com.carp.forum.exception.UnsupportedFileTypeException;
import com.carp.forum.repository.FileInfoRepository;
import com.carp.forum.repository.PostRepository;
import com.carp.forum.repository.ThreadRepository;
import com.carp.forum.repository.UserRepository;
import com.carp.forum.service.FileService;
import com.carp.forum.service.IPostService;
import com.carp.forum.tools.DtoTools;
import com.carp.forum.tools.JwtTokenUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class PostServiceImpl implements IPostService {

	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private ThreadRepository threadRepository;
	@Autowired
	private FileService fileService;
	@Autowired
	private FileInfoRepository fileInfoRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(PostServiceImpl.class);

	@Override
	public Set<Post> findMultiplePostsByIdAndByThreadId(Set<Long> ids, long threadId) {

		List<Post> resultInDb = postRepository.findAllByIdAndByThreadId(ids, threadId);
		Set<Post> result = Set.copyOf(resultInDb);
		return result;
	}

	@Override
	public PostDto save(PostDto post) throws TokenException, ForbiddenActionException, EntityNotFoundException {
		Post entityToSave = DtoTools.convert(post, Post.class);

		// check if thread exists
		if (!threadRepository.existsById(post.getThreadId())) {
			throw new EntityNotFoundException("Thread not found");
		}

		if (post.getUserId() != null) { // check if userId is specified in the post
			String headerAuth = request.getHeader("Authorization");
			if (headerAuth == null) {
				throw new TokenException("No Authorization header found but post contains userId");
			}
			String token = headerAuth.substring(7);
			Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
			long userId = claims.get("user_id", Long.class);
			String userType = claims.get("user_type", String.class);
			if (userId != post.getUserId() && !userType.equals("ADMIN")) {
				throw new ForbiddenActionException("Unauthorized");
			}
			Optional<User> optU = userRepository.findById(post.getUserId());
			if (optU.isEmpty()) {
				throw new EntityNotFoundException("User with id " + post.getUserId() + " not found");
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
					throw new EntityNotFoundException("User with id " + post.getUserId() + " not found");
				}
				entityToSave.setUser(optU.get());
			}

		}

		// fetch replied posts, only if they are in the same thread
		entityToSave.setReplyTo(this.findMultiplePostsByIdAndByThreadId(post.getReplyTo(), post.getThreadId()));
		entityToSave = postRepository.saveAndFlush(entityToSave);
		PostDto entitySaved = DtoTools.convert(entityToSave, PostDto.class);
		return entitySaved;
	}

	@Transactional
	@Override
	public PostDto saveWithImage(PostDto post, List<MultipartFile> files)
			throws TokenException, ForbiddenActionException, EntityNotFoundException {
		Post entityToSave = DtoTools.convert(post, Post.class);

		// check if thread exists
		if (!threadRepository.existsById(post.getThreadId())) {
			throw new EntityNotFoundException("Thread not found");
		}

		if (post.getUserId() != null) { // check if userId is specified in the post
			String headerAuth = request.getHeader("Authorization");
			if (headerAuth == null) {
				throw new TokenException("No Authorization header found but post contains userId");
			}
			String token = headerAuth.substring(7);
			Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
			long userId = claims.get("user_id", Long.class);
			String userType = claims.get("user_type", String.class);
			if (userId != post.getUserId() && !userType.equals("ADMIN")) {
				throw new ForbiddenActionException("Unauthorized");
			}
			Optional<User> optU = userRepository.findById(post.getUserId());
			if (optU.isEmpty()) {
				throw new EntityNotFoundException("User with id " + post.getUserId() + " not found");
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
					throw new EntityNotFoundException("User with id " + post.getUserId() + " not found");
				}
				entityToSave.setUser(optU.get());
			}

		}

		// fetch replied posts, only if they are in the same thread
		entityToSave.setReplyTo(this.findMultiplePostsByIdAndByThreadId(post.getReplyTo(), post.getThreadId()));
		entityToSave = postRepository.saveAndFlush(entityToSave);
		PostDto entitySaved = DtoTools.convert(entityToSave, PostDto.class);
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
	public List<PostDto> findPageByThreadId(long threadId, int page, int max) {
		List<Post> resultInDb = postRepository.findPageByThreadId(threadId, PageRequest.of(page, max)).get()
				.collect(Collectors.toList());
		List<PostDto> result = new ArrayList<>();
		for (Post post : resultInDb) {

			PostDto item = DtoTools.convert(post, PostDto.class);
			List<FileInfo> fileInfos = fileInfoRepository.findFileInfoByPostId(post.getId());
			List<FileInfoDto> fileInfodtos = new ArrayList<>();
			for (FileInfo fileInfo : fileInfos) {
				fileInfodtos.add(DtoTools.convert(fileInfo, FileInfoDto.class));
			}
			item.setFiles(fileInfodtos);
			result.add(item);
		}
		return result;
	}

	@Override
	public PostDto findById(long id) {
		Optional<Post> resultInDb = postRepository.findById(id);

		if (resultInDb.isPresent()) {

			PostDto result = DtoTools.convert(resultInDb.get(), PostDto.class);

			List<FileInfo> fileInfos = fileInfoRepository.findFileInfoByPostId(id);
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
		if (postRepository.existsById(id)) {
			List<FileInfo> fileInfos = fileInfoRepository.findFileInfoByPostId(id);
			for (FileInfo fileInfo : fileInfos) {
				fileService.delete(fileInfo.getFileName());
			}
			fileInfoRepository.deleteByPostId(id);

			postRepository.deleteById(id);
			return id;
		}
		return null;
	}

	@Override
	public List<PostDto> findAll(int page, int max, String search) {
		List<Post> resultInDb = postRepository.findAll(search, PageRequest.of(page, max)).get()
				.collect(Collectors.toList());
		List<PostDto> result = new ArrayList<>();
		for (Post post : resultInDb) {
			PostDto p = DtoTools.convert(post, PostDto.class);
			List<FileInfo> fileInfos = fileInfoRepository.findFileInfoByPostId(post.getId());
			List<FileInfoDto> fileInfodtos = new ArrayList<>();
			for (FileInfo fileInfo : fileInfos) {
				fileInfodtos.add(DtoTools.convert(fileInfo, FileInfoDto.class));
			}
			p.setFiles(fileInfodtos);
			result.add(p);
		}
		return result;
	}

	@Override
	public PostDto update(PostDto post) throws EntityNotFoundException, TokenException, ForbiddenActionException {
		Optional<Post> postInDb = postRepository.findById(post.getId());
		String headerAuth = request.getHeader("Authorization");
		if (headerAuth == null) {
			throw new TokenException("No Authorization header");
		}
		String token = headerAuth.substring(7);
		Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
		long userId = claims.get("user_id", Long.class);
		String userType = claims.get("user_type", String.class);


		if (postInDb.isPresent()) {

			
			if (!userType.equals("ADMIN")
					&& (postInDb.get().getUser() == null || postInDb.get().getUser().getId() != userId ||  (post.getUserId()!=null && post.getUserId()!=userId))) {
				throw new ForbiddenActionException();
			}
			if (!threadRepository.existsById(post.getThreadId())) {
				throw new EntityNotFoundException("Thread not found");
			}
			Post postToSave = DtoTools.convert(post, Post.class);
			postToSave.setReplyTo(this.findMultiplePostsByIdAndByThreadId(post.getReplyTo(), post.getThreadId()));

			if (userType.equals("ADMIN")) {

				if (post.getUserId() != null) {
					Optional<User> optU = userRepository.findById(post.getUserId());
					if (optU.isEmpty()) {
						throw new EntityNotFoundException("User with id " + post.getUserId() + " not found");
					}
					postToSave.setUser(optU.get());
				} else {
					if (postInDb.get().getUser() != null) {
						postToSave.setUser(postInDb.get().getUser());
					} else {
						postToSave.setUser(null);
					}
				}
			} else {
			
				postToSave.setUser(postInDb.get().getUser());
			}
			postToSave.setCreationDate(postInDb.get().getCreationDate());
			postToSave = postRepository.saveAndFlush(postToSave);
			return DtoTools.convert(postToSave, PostDto.class);
		}

		return null;
	}

	@Override
	public int countPostsByThreadId(long threadId) {
		return postRepository.coutPostsByThreadId(threadId);
	}

}
