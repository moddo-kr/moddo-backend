package com.dnd.moddo.domain.auth.controller;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.dnd.moddo.auth.presentation.AuthRedirectResolver;

class AuthRedirectResolverTest {

	private final AuthRedirectResolver authRedirectResolver = new AuthRedirectResolver();

	@Test
	@DisplayName("허용된 origin의 state는 그대로 리다이렉트한다.")
	void resolveAllowedState() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServerName("api.moddo.kr");

		String redirectUrl = authRedirectResolver.resolve(
			"https://www.moddo.kr/login/callback?next=%2Fhome",
			request
		);

		assertThat(redirectUrl).isEqualTo("https://www.moddo.kr/login/callback?next=%2Fhome");
	}

	@Test
	@DisplayName("허용되지 않은 origin의 state는 로컬 요청일 때 localhost로 대체한다.")
	void fallbackToLocalUrlForInvalidOrigin() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServerName("localhost");

		String redirectUrl = authRedirectResolver.resolve(
			"https://malicious.example.com/login/callback",
			request
		);

		assertThat(redirectUrl).isEqualTo("http://localhost:3000");
	}

	@Test
	@DisplayName("허용되지 않은 origin의 state는 운영 요청일 때 운영 URL로 대체한다.")
	void fallbackToProdUrlForInvalidOrigin() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServerName("api.moddo.kr");

		String redirectUrl = authRedirectResolver.resolve(
			"https://malicious.example.com/login/callback",
			request
		);

		assertThat(redirectUrl).isEqualTo("https://www.moddo.kr");
	}

	@Test
	@DisplayName("유효하지 않은 state는 요청 환경에 맞는 기본 URL로 대체한다.")
	void fallbackForMalformedState() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServerName("localhost");

		String redirectUrl = authRedirectResolver.resolve("not-a-valid-url", request);

		assertThat(redirectUrl).isEqualTo("http://localhost:3000");
	}
}
