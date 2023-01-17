package com.carp.forum.service;

import java.util.List;

import com.carp.forum.dto.UserDto;

public interface IUserService {
	UserDto save(UserDto user);

	UserDto findById(long id);

	void deleteById(long id);

	List<UserDto> findAll(int page, int max, String search);

	UserDto update(UserDto user);
}
