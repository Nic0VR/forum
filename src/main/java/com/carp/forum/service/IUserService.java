package com.carp.forum.service;

import java.util.List;

import com.carp.forum.dto.LoginDto;
import com.carp.forum.dto.LoginResponseDto;
import com.carp.forum.dto.UserDto;
import com.carp.forum.exception.DuplicateEntryException;
import com.carp.forum.exception.ForbiddenActionException;
import com.carp.forum.exception.TokenException;

public interface IUserService {
	UserDto save(UserDto user) throws DuplicateEntryException, TokenException, ForbiddenActionException;

	UserDto findById(long id);

	Long deleteById(long id);

	List<UserDto> findAll(int page, int max, String search);

	UserDto update(UserDto user) throws TokenException, ForbiddenActionException, DuplicateEntryException;

	LoginResponseDto checkLogin(LoginDto loginDto) throws Exception;
}
