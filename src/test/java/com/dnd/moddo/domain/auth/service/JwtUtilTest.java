package com.dnd.moddo.domain.auth.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.dnd.moddo.auth.infrastructure.security.JwtProperties;
import com.dnd.moddo.auth.infrastructure.security.JwtUtil;

import jakarta.servlet.http.Cookie;

class JwtUtilTest {

	private final JwtUtil jwtUtil = new JwtUtil(
		new JwtProperties(
			"Authorization",
			"Bearer",
			"accessToken",
			"c2VjcmV0S2V5c2VjcmV0S2V5c2VjcmV0S2V5c2VjcmV0S2V5c2VjcmV0S2V5",
			1L,
			1L
		)
	);

	@Test
	void givenAccessTokenCookie_thenResolveTokenFromCookie() {
		// given
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setCookies(new Cookie("accessToken", "cookie-token"));
		request.addHeader("Authorization", "Bearer header-token");

		// when
		String token = jwtUtil.resolveToken(request);

		// then
		assertThat(token).isEqualTo("cookie-token");
	}

	@Test
	void givenAuthorizationHeaderWithoutCookie_thenResolveTokenFromHeader() {
		// given
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer header-token");

		// when
		String token = jwtUtil.resolveToken(request);

		// then
		assertThat(token).isEqualTo("header-token");
	}

	@Test
	void givenBlankAccessTokenCookie_thenResolveTokenFromHeader() {
		// given
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setCookies(new Cookie("accessToken", " "));
		request.addHeader("Authorization", "Bearer header-token");

		// when
		String token = jwtUtil.resolveToken(request);

		// then
		assertThat(token).isEqualTo("header-token");
	}
}
