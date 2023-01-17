package com.carp.forum.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.carp.forum.Exception.TokenException;
import com.carp.forum.tools.JwtTokenUtil;
import com.carp.forum.tools.TokenSaver;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor implements HandlerInterceptor {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//System.out.println("inside token interceptor");
		
		if(!request.getMethod().equals("OPTIONS")) {//on ne doit pas interception les req : OPTIONS
			// todo: add allowed urls for unidentified user
			if(!request.getRequestURI().equals("/login")
				&& !request.getRequestURI().equals("/reset-password")
				&& !request.getRequestURI().equals("/change-password")
					) {//ni les req /login
				//récupérer l'entête Authorization (bearer TOKENNNNN)
				String headerAuth = request.getHeader("Authorization");
				// si aucun jeton ou non conforme <7 => exception jeton absent ou non valide
				if(headerAuth==null || headerAuth.trim().equals("") || headerAuth.length()<7)
					throw new TokenException("Error : Invalid token !");
				
				//	découpe pour extraire le jeton
				String token = headerAuth.substring(7);
				//	valider le jeton en vérifiant qu'il n'a pas expiré
				if(jwtTokenUtil.isTokenExpired(token))
					throw new TokenException("Error : Expired token !");
				
				//vérifier que c'est un jeton qui a été stocké dans la map (TokenSaver)
				String email = jwtTokenUtil.getUsernameFromToken(token);
				if(!TokenSaver.tokensByEmail.containsKey(email) 
						|| !TokenSaver.tokensByEmail.get(email).equals(token)) {
						throw new TokenException("Error : Unknown token !");
				}
				
				

				//Autre : ....
				Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
				//TODO: allow specific URI for admin
			}
		}
		
		
		
		
		return true;
	}

	
}