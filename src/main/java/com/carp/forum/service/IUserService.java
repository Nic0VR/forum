package com.carp.forum.service;

import java.util.List;

import com.carp.forum.dto.LoginDto;
import com.carp.forum.dto.LoginResponseDto;
import com.carp.forum.dto.UserDto;
import com.carp.forum.exception.DuplicateEntryException;

public interface IUserService {
	UserDto save(UserDto user) throws DuplicateEntryException;

	UserDto findById(long id);

	void deleteById(long id);

	List<UserDto> findAll(int page, int max, String search);

	UserDto update(UserDto user);

	LoginResponseDto checkLogin(LoginDto loginDto) throws Exception;
}
