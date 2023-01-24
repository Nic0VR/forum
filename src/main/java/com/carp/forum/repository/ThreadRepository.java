package com.carp.forum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carp.forum.entities.Thread;

public interface ThreadRepository extends JpaRepository<Thread,Long>{

	@Query("FROM Thread t WHERE t.board.id=:boardId AND (t.title LIKE %:search% OR t.text LIKE %:search%)")
	Page<Thread> findPageByBoardId(@Param("boardId")long boardId,@Param("search")String search, Pageable pageable);
	
	@Query("FROM Thread t WHERE t.title LIKE %:search% OR t.text LIKE %:search%")
	Page<Thread> findPage(@Param("search")String search, Pageable pageable);

}
