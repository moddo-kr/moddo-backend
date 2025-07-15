package com.dnd.moddo.global.jwt.service;

import org.springframework.stereotype.Service;

import com.dnd.moddo.global.jwt.utill.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
	private final JwtUtil jwtUtil;

	public Long getId(HttpServletRequest request, String key) {
		String token = jwtUtil.resolveToken(request);
		return jwtUtil.getIdFromToken(token, key);
	}

	public Long getUserId(HttpServletRequest request) {
		return getId(request, "userId");
	}

	public Long getUserId(String token) {
		return jwtUtil.getIdFromToken(token, "userId");
	}

	public Long getGroupId(String groupToken) {
		return jwtUtil.getIdFromToken(groupToken, "groupId");
	}
}
