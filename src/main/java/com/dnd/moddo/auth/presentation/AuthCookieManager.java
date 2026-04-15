package com.dnd.moddo.auth.presentation;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.dnd.moddo.common.config.CookieProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthCookieManager {

	private final CookieProperties cookieProperties;

	public String createCookie(String name, String value) {
		return ResponseCookie.from(name, value)
			.httpOnly(cookieProperties.httpOnly())
			.secure(cookieProperties.secure())
			.path(cookieProperties.path())
			.sameSite(cookieProperties.sameSite())
			.maxAge(cookieProperties.maxAge())
			.build()
			.toString();
	}

	public String expireCookie(String name) {
		return ResponseCookie.from(name, null)
			.httpOnly(cookieProperties.httpOnly())
			.secure(cookieProperties.secure())
			.path(cookieProperties.path())
			.sameSite(cookieProperties.sameSite())
			.maxAge(0L)
			.build()
			.toString();
	}
}
