package com.carp.forum.interceptor;

import java.util.Optional;

import org.hibernate.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.carp.forum.entities.User;
import com.carp.forum.repository.UserRepository;
import com.carp.forum.tools.JwtTokenUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserInjectorInterceptor implements Interceptor {
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	public User getUser() {
		String headerAuth = request.getHeader("Authorization");
		String token = headerAuth.substring(7);
		Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
		long userId = claims.get("user_id", Long.class);
		Optional<User> optU = userRepository.findById(userId);
		if (optU.isPresent())
			return optU.get();

		return null;
	}
	
	
}
