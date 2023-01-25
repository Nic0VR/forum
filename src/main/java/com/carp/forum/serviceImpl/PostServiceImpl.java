package com.carp.forum.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.carp.forum.dto.PostDto;
import com.carp.forum.entities.Post;
import com.carp.forum.entities.User;
import com.carp.forum.exception.EntityNotFoundException;
import com.carp.forum.exception.ForbiddenActionException;
import com.carp.forum.exception.TokenException;
import com.carp.forum.exception.UnsupportedFileTypeException;
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
	@Override
	public Set<Post> findMultiplePostsByIdAndByThreadId(Set<Long> ids, long threadId){
		
		List<Post> resultInDb = postRepository.findAllByIdAndByThreadId(ids, threadId);
		Set<Post> result = Set.copyOf(resultInDb);
		return result;
	}
	
	@Override
	public PostDto save(PostDto post) throws TokenException, ForbiddenActionException, EntityNotFoundException {
		Post entityToSave = DtoTools.convert(post, Post.class);
		
		//check if thread exists
		if(!threadRepository.existsById(post.getThreadId()) ) {
			throw new EntityNotFoundException("Thread not found");
		}
		
		if(post.getUserId()!=null) { // check if userId is specified in the post
			String headerAuth = request.getHeader("Authorization");
			if(headerAuth == null) {
				throw new TokenException("No Authorization header found but post contains userId");
			}
			String token = headerAuth.substring(7);
			Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
			long userId = claims.get("user_id", Long.class);
			String userType = claims.get("user_type",String.class);
			if(userId != post.getUserId() && !userType.equals("ADMIN")) {
				throw new ForbiddenActionException("Unauthorized");
			}
			Optional<User> optU = userRepository.findById(post.getUserId());
			if (optU.isEmpty()) {
				throw new EntityNotFoundException("User with id "+post.getUserId()+" not found");
			}
			entityToSave.setUser(optU.get());
		}else { // try to assign post to logged user if user is logged
			String headerAuth = request.getHeader("Authorization");
			if(headerAuth != null) {
				String token = headerAuth.substring(7);
				Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
				long userId = claims.get("user_id", Long.class);
				Optional<User> optU = userRepository.findById(userId);
				if(optU.isEmpty()) {
					throw new EntityNotFoundException("User with id "+post.getUserId()+" not found");
				}
				entityToSave.setUser(optU.get());
			}
			
		}
		
		// fetch replied posts, only if they are in the same thread
		entityToSave.setReplyTo(this.findMultiplePostsByIdAndByThreadId(post.getReplyTo(),post.getThreadId()));
		entityToSave = postRepository.saveAndFlush(entityToSave);
		PostDto entitySaved = DtoTools.convert(entityToSave, PostDto.class);
		return entitySaved;
	}
	
	@Transactional
	@Override
	public PostDto saveWithImage(PostDto post,List<MultipartFile> files) throws TokenException, ForbiddenActionException, EntityNotFoundException {
		Post entityToSave = DtoTools.convert(post, Post.class);
		
		//check if thread exists
		if(!threadRepository.existsById(post.getThreadId()) ) {
			throw new EntityNotFoundException("Thread not found");
		}
		
		if(post.getUserId()!=null) { // check if userId is specified in the post
			String headerAuth = request.getHeader("Authorization");
			if(headerAuth == null) {
				throw new TokenException("No Authorization header found but post contains userId");
			}
			String token = headerAuth.substring(7);
			Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
			long userId = claims.get("user_id", Long.class);
			String userType = claims.get("user_type",String.class);
			if(userId != post.getUserId() && !userType.equals("ADMIN")) {
				throw new ForbiddenActionException("Unauthorized");
			}
			Optional<User> optU = userRepository.findById(post.getUserId());
			if (optU.isEmpty()) {
				throw new EntityNotFoundException("User with id "+post.getUserId()+" not found");
			}
			entityToSave.setUser(optU.get());
		}else { // try to assign post to logged user if user is logged
			String headerAuth = request.getHeader("Authorization");
			if(headerAuth != null) {
				String token = headerAuth.substring(7);
				Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
				long userId = claims.get("user_id", Long.class);
				Optional<User> optU = userRepository.findById(userId);
				if(optU.isEmpty()) {
					throw new EntityNotFoundException("User with id "+post.getUserId()+" not found");
				}
				entityToSave.setUser(optU.get());
			}
			
		}
		
		// fetch replied posts, only if they are in the same thread
		entityToSave.setReplyTo(this.findMultiplePostsByIdAndByThreadId(post.getReplyTo(),post.getThreadId()));
		entityToSave = postRepository.saveAndFlush(entityToSave);
		PostDto entitySaved = DtoTools.convert(entityToSave, PostDto.class);
		
		// File logic
		for (MultipartFile multipartFile : files) {

			try {
				fileService.saveFile(multipartFile, entityToSave);
			} catch (IOException | UnsupportedFileTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return entitySaved;
	}
	
	@Override
	public List<PostDto> findPageByThreadId(long threadId,int page,int max){
		List<Post> resultInDb = postRepository.findPageByThreadId(threadId, PageRequest.of(page, max)).get().collect(Collectors.toList());
		List<PostDto> result = new ArrayList<>();
		for (Post post : resultInDb) {
			PostDto item = DtoTools.convert(post, PostDto.class);
			result.add(item);
		}
		return result;
	}

	@Override
	public PostDto findById(long id) {
		Optional<Post> resultInDb = postRepository.findById(id);
		if(resultInDb.isPresent()) {
			PostDto result = DtoTools.convert(resultInDb.get(), PostDto.class);
			return result;
		}
		return null;
	}

	@Override
	public Long deleteById(long id) {
		if(postRepository.existsById(id)) {
			postRepository.deleteById(id);
			return id;
		}
		return null;
	}

	@Override
	public List<PostDto> findAll(int page, int max, String search) {
		List<Post> resultInDb = postRepository.findAll(search,PageRequest.of(page, max)).get().collect(Collectors.toList());
		List<PostDto> result = new ArrayList<>();
		for (Post post : resultInDb) {
			PostDto p = DtoTools.convert(post, PostDto.class);
			result.add(p);
		}
		return result;
	}

	@Override
	public PostDto update(PostDto post) throws EntityNotFoundException, TokenException, ForbiddenActionException {
		Optional<Post> postInDb = postRepository.findById(post.getId());
		if(!threadRepository.existsById(post.getThreadId())) {
			throw new EntityNotFoundException("Thread not found");
		}
		if(postInDb.isPresent()) {
			Post postToSave = DtoTools.convert(post, Post.class);
			postToSave.setReplyTo(this.findMultiplePostsByIdAndByThreadId(post.getReplyTo(),post.getThreadId()));
			

			if(post.getUserId()!=null) {
				String headerAuth = request.getHeader("Authorization");
				if(headerAuth == null) {
					throw new TokenException("No Authorization header found but post contains userId");
				}
				String token = headerAuth.substring(7);
				Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
				long userId = claims.get("user_id", Long.class);
				String userType = claims.get("user_type",String.class);
				if(userId != post.getUserId() && !userType.equals("ADMIN")) {
					throw new ForbiddenActionException();
				}
				Optional<User> optU = userRepository.findById(post.getUserId());
				if (optU.isEmpty()) {
					throw new EntityNotFoundException("User with id "+post.getUserId()+" not found");
				}
				postToSave.setUser(optU.get());
			}
			
			postToSave = postRepository.saveAndFlush(postToSave);
			return DtoTools.convert(postToSave, PostDto.class);
		}
		
		return null;
	}
	
}
