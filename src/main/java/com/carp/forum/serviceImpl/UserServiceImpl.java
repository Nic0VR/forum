package com.carp.forum.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carp.forum.dto.UserDto;
import com.carp.forum.entities.User;
import com.carp.forum.repository.UserRepository;
import com.carp.forum.service.IUserService;
import com.carp.forum.tools.DtoTools;
@Service
public class UserServiceImpl implements IUserService{
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDto save(UserDto user) {
		User entityToSave = DtoTools.convert(user, User.class);
		entityToSave = userRepository.saveAndFlush(entityToSave);
		UserDto entitySaved = DtoTools.convert(entityToSave, UserDto.class);
		return entitySaved;
	}

	@Override
	public UserDto findById(long id) {
		Optional<User> resultInDb = userRepository.findById(id);
		if(resultInDb.isPresent()) {
			UserDto result = DtoTools.convert(resultInDb.get(), UserDto.class);
			return result;
		}
		return null;
	}

	@Override
	public void deleteById(long id) {
		userRepository.deleteById(id);
	}

	@Override
	public List<UserDto> findAll(int page, int max, String search) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDto update(UserDto user) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
