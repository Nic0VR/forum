package com.carp.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carp.forum.entities.Thread;

public interface ThreadRepository extends JpaRepository<Thread,Long>{

}
