package com.carp.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carp.forum.entities.Board;

public interface BoardRepository extends JpaRepository<Board,Long>{

}
