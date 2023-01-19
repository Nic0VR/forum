package com.carp.forum.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carp.forum.entities.User;

public interface UserRepository extends JpaRepository<User,Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByUsername(String username);
	
	@Query("FROM User u WHERE (u.username LIKE %:search% OR u.email LIKE %:search%) AND u.role <>'ADMIN'")
	Page<User> findAllUsersByUsernameOrEmailContaining(@Param("search")String search, Pageable pageable);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

}
