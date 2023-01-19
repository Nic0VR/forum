package com.carp.forum.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carp.forum.entities.Post;

public interface PostRepository extends JpaRepository<Post,Long>{

	@Query("FROM Post p LEFT JOIN FETCH p.replyTo LEFT JOIN FETCH p.repliedBy WHERE p.id=:id ")
	Optional<Post> findById(@Param("id")long id);
	
	@Query("FROM Post p WHERE p.id IN :ids AND p.thread.id=:threadId")
	List<Post> findAllByIdAndByThreadId(@Param("ids")Set<Long> ids, @Param("threadId")long threadId);
	
	@Query("FROM Post p WHERE p.thread.id=:threadId")
	Page<Post> findPageByThreadId(@Param("threadId")long threadId,Pageable pageable);
	
	@Query("FROM Post p WHERE p.text LIKE %:search%")
	Page<Post> findAll(@Param("search")String search, PageRequest of);
}
