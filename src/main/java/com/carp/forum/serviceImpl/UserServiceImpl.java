package com.carp.forum.serviceImpl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carp.forum.dto.LoginDto;
import com.carp.forum.dto.LoginResponseDto;
import com.carp.forum.dto.UserDto;
import com.carp.forum.entities.User;
import com.carp.forum.exception.DuplicateEntryException;
import com.carp.forum.repository.UserRepository;
import com.carp.forum.service.IUserService;
import com.carp.forum.tools.DtoTools;
import com.carp.forum.tools.HashTools;
import com.carp.forum.tools.JwtTokenUtil;
import com.carp.forum.tools.TokenSaver;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	public UserDto save(UserDto user) throws DuplicateEntryException {
		User entityToSave = DtoTools.convert(user, User.class);

		Optional<User> userByUsername = userRepository.findByUsername(user.getUsername());
		if(userByUsername.isPresent()) {
			throw new DuplicateEntryException("This username is already used");
		}
		Optional<User> userByEmail = userRepository.findByEmail(user.getEmail());
		if(userByEmail.isPresent()) {
			throw new DuplicateEntryException("This email is already used");
		}
		
		try {
			entityToSave.setPassword(HashTools.hashSHA512(entityToSave.getPassword()));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new AssertionError("Error occured during encoding");
		}
		
		entityToSave = userRepository.saveAndFlush(entityToSave);
		UserDto entitySaved = DtoTools.convert(entityToSave, UserDto.class);
		return entitySaved;
	}

	@Override
	public UserDto findById(long id) {
		Optional<User> resultInDb = userRepository.findById(id);
		if (resultInDb.isPresent()) {
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

	@Override
	public LoginResponseDto checkLogin(LoginDto loginDto) throws Exception {
		Optional<User> user = userRepository.findByEmail(loginDto.getEmail());
		if (user.isPresent() && user.get().getPassword().equals(HashTools.hashSHA512(loginDto.getPassword()))) {
			LoginResponseDto result = DtoTools.convert(user, LoginResponseDto.class);
			Map<String, Object> claims = new HashMap<String, Object>();
			claims.put("user_id", user.get().getId());
			claims.put("user_username", user.get().getUsername());
			claims.put("user_type", user.get().getRole().toString());
			String token = jwtTokenUtil.doGenerateToken(claims, loginDto.getEmail());

			TokenSaver.tokensByEmail.put(user.get().getEmail(), token);
			result.setId(user.get().getId());
			result.setUsername(user.get().getUsername());
			result.setToken(token);
			result.setTypeUser(user.get().getRole().toString());
			return result;
		} else {
			throw new Exception("Erreur d'Authentification");
		}
	}
}
