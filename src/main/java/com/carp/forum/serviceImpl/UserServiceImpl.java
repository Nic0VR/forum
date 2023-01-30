package com.carp.forum.serviceImpl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.carp.forum.dto.LoginDto;
import com.carp.forum.dto.LoginResponseDto;
import com.carp.forum.dto.UserDto;
import com.carp.forum.entities.User;
import com.carp.forum.enums.Role;
import com.carp.forum.exception.DuplicateEntryException;
import com.carp.forum.exception.ForbiddenActionException;
import com.carp.forum.exception.TokenException;
import com.carp.forum.repository.UserRepository;
import com.carp.forum.service.IUserService;
import com.carp.forum.tools.DtoTools;
import com.carp.forum.tools.HashTools;
import com.carp.forum.tools.JwtTokenUtil;
import com.carp.forum.tools.TokenSaver;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private HttpServletRequest request;

	@Override
	public UserDto save(UserDto user) throws DuplicateEntryException, TokenException, ForbiddenActionException {
		User entityToSave = DtoTools.convert(user, User.class);
		if (user.getRole().equals(Role.ADMIN)) {
			String headerAuth = request.getHeader("Authorization");
			if (headerAuth == null) {
				throw new TokenException("No authentication token provided");
			}
			String token = headerAuth.substring(7);
			Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
			String userType = claims.get("user_type", String.class);
			if (!userType.equals("ADMIN")) {
				throw new ForbiddenActionException("Must be admin to create admin user");
			}
		}

		Optional<User> userByUsername = userRepository.findByUsername(user.getUsername());
		if (userByUsername.isPresent()) {
			throw new DuplicateEntryException("This username is already used");
		}
		Optional<User> userByEmail = userRepository.findByEmail(user.getEmail());
		if (userByEmail.isPresent()) {
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
	public Long deleteById(long id) {
		if (userRepository.existsById(id)) {
			userRepository.deleteById(id);
			return id;
		}
		return null;
	}

	@Override
	public List<UserDto> findAll(int page, int max, String search) {

		List<User> resultInDb = userRepository
				.findAllUsersByUsernameOrEmailContaining(search, PageRequest.of(page, max)).get()
				.collect(Collectors.toList());
		List<UserDto> result = new ArrayList<>();
		for (User user : resultInDb) {
			UserDto u = DtoTools.convert(user, UserDto.class);
			result.add(u);
		}
		return result;
	}

	@Override
	public UserDto update(UserDto user) throws TokenException, ForbiddenActionException, DuplicateEntryException {
		// check if user to update is the same as logged user or user is admin
		String headerAuth = request.getHeader("Authorization");
		if (headerAuth == null) {
			throw new TokenException("No Authorization header found");
		}
		String token = headerAuth.substring(7);
		Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
		long userId = claims.get("user_id", Long.class);
		String userType = claims.get("user_type", String.class);

		if (user.getId() != userId && !userType.equals("ADMIN")) {
			throw new ForbiddenActionException("Unauthorized");
		}
		Optional<User> userInDb = userRepository.findById(user.getId());
		if (userInDb.isPresent()) {
			User userToUpdate = DtoTools.convert(user, User.class);
			// check if update requires a modification of email and if new email is available
			if(!userToUpdate.getEmail().equals(userInDb.get().getEmail()) 
					&& userRepository.existsByEmail(userToUpdate.getEmail())) {
				throw new DuplicateEntryException("Email already used");
			}
			if(!userToUpdate.getUsername().equals(userInDb.get().getUsername()) 
					&& userRepository.existsByUsername(userToUpdate.getUsername())) {
				throw new DuplicateEntryException("Username already used");
			}
			
			try {
				if (!HashTools.hashSHA512(userToUpdate.getPassword()).equals(userInDb.get().getPassword())) {
					userToUpdate.setPassword(HashTools.hashSHA512(userToUpdate.getPassword()));
				}
			} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
				throw new AssertionError("Error occured during encoding");
			}
			if(userToUpdate.getRole().equals(Role.ADMIN) && !userType.equals("ADMIN") ) {
				throw new ForbiddenActionException("Unauthorized");
			}
			userToUpdate = userRepository.saveAndFlush(userToUpdate);
			return DtoTools.convert(userToUpdate, UserDto.class);
		}
		return null;
	}

	@Override
	public LoginResponseDto checkLogin(LoginDto loginDto) throws NoSuchAlgorithmException, UnsupportedEncodingException, TokenException {
		Optional<User> user = userRepository.findByEmail(loginDto.getEmail());
		if (user.isPresent() && user.get().getPassword().equals(HashTools.hashSHA512(loginDto.getPassword()))) {
			LoginResponseDto result = DtoTools.convert(user, LoginResponseDto.class);
			Map<String, Object> claims = new HashMap<String, Object>();
			claims.put("user_id", user.get().getId());
			claims.put("user_username", user.get().getUsername());
			claims.put("user_type", user.get().getRole().toString());
			String token = jwtTokenUtil.doGenerateToken(claims, user.get().getEmail());
			
			TokenSaver.tokensByEmail.put(user.get().getEmail(), token);
			result.setId(user.get().getId());
			result.setUsername(user.get().getUsername());
			result.setToken(token);
			result.setTypeUser(user.get().getRole().toString());
			return result;
		} else {
			throw new TokenException("Erreur d'Authentification");
		}
	}
}
