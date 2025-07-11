package com.dnd.moddo.domain.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.domain.auth.service.AuthService;
import com.dnd.moddo.domain.auth.service.RefreshTokenService;
import com.dnd.moddo.global.config.CookieProperties;
import com.dnd.moddo.global.jwt.dto.RefreshResponse;
import com.dnd.moddo.global.jwt.dto.TokenResponse;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1")
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;
	private final CookieProperties cookieProperties;

	@GetMapping("/user/guest/token")
	public ResponseEntity<TokenResponse> getGuestToken() {
		TokenResponse tokenResponse = authService.loginWithGuest();

		String cookie = createCookie("accessToken", tokenResponse.accessToken()).toString();

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookie)
			.body(tokenResponse);
	}

	@PutMapping("/user/reissue/token")
	public RefreshResponse reissueAccessToken(@RequestHeader(value = "Authorization") @NotBlank String refreshToken) {
		return refreshTokenService.execute(refreshToken);
	}

	@GetMapping("/login/oauth2/callback")
	public ResponseEntity<Void> kakaoLoginCallback(@RequestParam @NotBlank String code) {

		TokenResponse tokenResponse = authService.loginOrRegisterWithKakao(code);

		String cookie = createCookie("accessToken", tokenResponse.accessToken()).toString();

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookie)
			.build();
	}

	@GetMapping("/logout")
	public ResponseEntity<Void> kakaoLogout() {
		String cookie = expireCookie("accessToken").toString();

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookie)
			.build();
	}

	private ResponseCookie createCookie(String name, String key) {
		return ResponseCookie.from(name, key)
			.httpOnly(cookieProperties.httpOnly())
			.secure(cookieProperties.secure())
			.path(cookieProperties.path())
			.sameSite(cookieProperties.sameSite())
			.maxAge(cookieProperties.maxAge())
			.build();

	}

	private ResponseCookie expireCookie(String name) {
		return ResponseCookie.from(name, null)
			.httpOnly(cookieProperties.httpOnly())
			.secure(cookieProperties.secure())
			.path(cookieProperties.path())
			.sameSite(cookieProperties.sameSite())
			.maxAge(0L)
			.build();
	}

}