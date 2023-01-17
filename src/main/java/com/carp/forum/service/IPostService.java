package com.carp.forum.service;

import java.util.List;
import java.util.Set;

import com.carp.forum.dto.PostDto;
import com.carp.forum.entities.Post;

public interface IPostService {

	PostDto save(PostDto post);

	PostDto findById(long id);

	void deleteById(long id);

	List<PostDto> findAll(int page, int max, String search);

	PostDto update(PostDto post);

	Set<Post> findMultiplePostsById(Set<Long> ids);
}
