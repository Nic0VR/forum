package com.carp.forum.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carp.forum.entities.User;

public interface UserRepository extends JpaRepository<User,Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByUsername(String username);

}
