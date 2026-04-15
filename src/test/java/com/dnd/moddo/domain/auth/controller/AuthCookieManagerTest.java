package com.dnd.moddo.domain.auth.controller;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.auth.presentation.AuthCookieManager;
import com.dnd.moddo.common.config.CookieProperties;

class AuthCookieManagerTest {

	private final AuthCookieManager authCookieManager = new AuthCookieManager(
		new CookieProperties(true, true, "/", "none", Duration.ofDays(7))
	);

	@Test
	@DisplayName("인증 쿠키를 설정값에 맞춰 생성한다.")
	void createCookie() {
		String cookie = authCookieManager.createCookie("accessToken", "access-token");

		assertThat(cookie).contains("accessToken=access-token");
		assertThat(cookie).contains("Path=/");
		assertThat(cookie).contains("Max-Age=604800");
		assertThat(cookie).contains("Secure");
		assertThat(cookie).contains("HttpOnly");
		assertThat(cookie).contains("SameSite=none");
	}

	@Test
	@DisplayName("만료 쿠키를 생성한다.")
	void expireCookie() {
		String cookie = authCookieManager.expireCookie("accessToken");

		assertThat(cookie).contains("accessToken=");
		assertThat(cookie).contains("Path=/");
		assertThat(cookie).contains("Max-Age=0");
		assertThat(cookie).contains("Secure");
		assertThat(cookie).contains("HttpOnly");
		assertThat(cookie).contains("SameSite=none");
	}
}
