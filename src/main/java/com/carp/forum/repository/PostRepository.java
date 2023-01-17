package com.carp.forum.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.carp.forum.entities.Post;

public interface PostRepository extends JpaRepository<Post,Long>{

	@Query("FROM Post p LEFT JOIN FETCH p.replyTo LEFT JOIN FETCH p.repliedBy WHERE p.id=:id ")
	Optional<Post> findById(long id);
}
