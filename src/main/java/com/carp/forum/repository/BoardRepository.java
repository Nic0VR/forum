package com.carp.forum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carp.forum.entities.Board;

public interface BoardRepository extends JpaRepository<Board,Long>{

	@Query("FROM Board b WHERE b.ref LIKE %:search% OR b.description LIKE %:search% OR b.title LIKE %:search%")
	Page<Board> findAll(@Param("search")String search, Pageable pageable);

}
