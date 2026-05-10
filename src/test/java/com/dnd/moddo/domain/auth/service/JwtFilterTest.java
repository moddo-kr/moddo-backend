package com.dnd.moddo.domain.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dnd.moddo.auth.infrastructure.security.JwtAuth;
import com.dnd.moddo.auth.infrastructure.security.JwtFilter;
import com.dnd.moddo.auth.infrastructure.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

	@Mock
	JwtAuth jwtAuth;

	@Mock
	JwtUtil jwtUtil;

	@Mock
	FilterChain filterChain;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void givenExpiredToken_thenReturnTokenExpiredResponse() throws Exception {
		// given
		String token = "expired-token";
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		JwtFilter jwtFilter = new JwtFilter(jwtAuth, jwtUtil, objectMapper);

		given(jwtUtil.resolveToken(any(MockHttpServletRequest.class))).willReturn(token);
		given(jwtAuth.getAuthentication(token, "access_token"))
			.willThrow(new ExpiredJwtException(null, null, "expired"));

		// when
		jwtFilter.doFilter(request, response, filterChain);

		// then
		assertThat(response.getStatus()).isEqualTo(401);
		assertThat(response.getContentType()).startsWith("application/json");
		assertThat(response.getContentAsString()).contains("\"message\":\"토큰이 만료되었습니다.\"");
		then(filterChain).should(never()).doFilter(request, response);
	}
}
