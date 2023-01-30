package com.carp.forum.service;

import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.carp.forum.dto.PostDto;
import com.carp.forum.entities.Post;
import com.carp.forum.exception.BadPayloadException;
import com.carp.forum.exception.EntityNotFoundException;
import com.carp.forum.exception.ForbiddenActionException;
import com.carp.forum.exception.TokenException;

public interface IPostService {

	PostDto save(PostDto post) throws TokenException, ForbiddenActionException, EntityNotFoundException;

	PostDto findById(long id);

	Long deleteById(long id);

	List<PostDto> findAll(int page, int max, String search);

	PostDto update(PostDto post) throws TokenException, ForbiddenActionException,EntityNotFoundException;

	Set<Post> findMultiplePostsByIdAndByThreadId(Set<Long> ids, long threadId);

	List<PostDto> findPageByThreadId(long threadId, int page, int max);

	PostDto saveWithImage(PostDto post, List<MultipartFile> files)
			throws TokenException, ForbiddenActionException, EntityNotFoundException;
	
	int countPostsByThreadId(long threadId);
}
