package com.apiintegration.core.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.apiintegration.core.model.User;
import com.apiintegration.core.response.BasicResponse;
import com.apiintegration.core.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private UserService userService;

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");

		String username = null;
		String jwtToken = null;
		// JWT Token is in the form "Bearer token". Remove Bearer word and get
		// only the Token
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				System.out.println("JWT Token has expired");
				sendResponse(response, HttpStatus.UNAUTHORIZED, "Your session has expired! Please log in again.");
			}
		}

		// Once we get the token validate it.
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = userService.loadUserByUsername(username);

			// if token is valid configure Spring Security to manually set
			// authentication
			if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
				if (((UserDetailsImpl) userDetails).isSessionValid(jwtTokenUtil.getSessionIdFromToken(jwtToken))) {
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken
							.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					// After setting the Authentication in the context, we specify
					// that the current user is authenticated. So it passes the
					// Spring Security Configurations successfully.
					User user = userService.getUserByEmail(username);
					request.setAttribute("user", user );
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				} else {
					sendResponse(response, HttpStatus.UNAUTHORIZED, "Your have been logged out of this device.");
					return;
				}
			}
		}
		chain.doFilter(request, response);
	}

	private void sendResponse(HttpServletResponse response, HttpStatus responesStatus, String message)
			throws IOException {
		response.setStatus(responesStatus.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		MAPPER.writeValue(response.getWriter(), new BasicResponse(message, "", HttpStatus.LOCKED.value()));
	}
}