package com.carp.forum.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carp.forum.dto.PostDto;
import com.carp.forum.entities.Post;
import com.carp.forum.repository.PostRepository;
import com.carp.forum.service.IPostService;
import com.carp.forum.tools.DtoTools;

import jakarta.transaction.Transactional;
@Service
public class PostServiceImpl implements IPostService {
	
	@Autowired
	private PostRepository postRepository;
	@Override
	public Set<Post> findMultiplePostsById(Set<Long> ids){
		
		List<Post> resultInDb = postRepository.findAllById(ids);
		Set<Post> result = Set.copyOf(resultInDb);
		return result;
	}

	@Override
	public PostDto save(PostDto post) {
		Post entityToSave = DtoTools.convert(post, Post.class);
		// fetch post citeds in the post
		entityToSave.setReplyTo(this.findMultiplePostsById(post.getReplyTo()));
		
		entityToSave = postRepository.saveAndFlush(entityToSave);
		PostDto entitySaved = DtoTools.convert(entityToSave, PostDto.class);
		return entitySaved;
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
	public void deleteById(long id) {
		postRepository.deleteById(id);
	}

	@Override
	public List<PostDto> findAll(int page, int max, String search) {
		List<Post> resultInDb = postRepository.findAll();
		List<PostDto> result = new ArrayList<>();
		for (Post post : resultInDb) {
			PostDto p = DtoTools.convert(post, PostDto.class);
			result.add(p);
		}
		return result;
	}

	@Override
	public PostDto update(PostDto post) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
