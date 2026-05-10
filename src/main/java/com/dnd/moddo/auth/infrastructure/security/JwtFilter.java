package com.dnd.moddo.auth.infrastructure.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dnd.moddo.common.exception.ErrorResponse;
import com.dnd.moddo.common.exception.ModdoException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtAuth jwtAuth;
	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String token = jwtUtil.resolveToken(request);

		try {
			if (token != null) {
				Authentication authentication = jwtAuth.getAuthentication(token, JwtConstants.ACCESS_KEY.message);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (ExpiredJwtException e) {
			SecurityContextHolder.clearContext();
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다.");
			return;
		} catch (JwtException e) {
			SecurityContextHolder.clearContext();
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다.");
			return;
		} catch (ModdoException e) {
			SecurityContextHolder.clearContext();
			writeErrorResponse(response, e.getStatus(), e.getMessage());
			return;
		}

		filterChain.doFilter(request, response);
	}

	private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getWriter(), new ErrorResponse(status.value(), message));
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return request.getRequestURI().startsWith("/api/v1/user/reissue/token");
	}
}
