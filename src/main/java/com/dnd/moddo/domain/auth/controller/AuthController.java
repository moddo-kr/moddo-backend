package com.dnd.moddo.domain.auth.controller;

import java.io.IOException;
import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
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
import com.dnd.moddo.global.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletResponse;
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
	private final JwtService jwtService;

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
	public void kakaoLoginCallback(@RequestParam @NotBlank String code,
		HttpServletResponse response) throws
		IOException {

		TokenResponse tokenResponse = authService.loginOrRegisterWithKakao(code);

		String cookie = createCookie("accessToken", tokenResponse.accessToken()).toString();
		response.addHeader("Set-Cookie", cookie);
		response.sendRedirect("https://www.moddo.kr");
	}

	@GetMapping("/logout")
	public ResponseEntity<?> kakaoLogout(@CookieValue(value = "accessToken") String token) {
		String cookie = expireCookie("accessToken").toString();
		Long userId = jwtService.getUserId(token);
		authService.logout(userId);
		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookie)
			.body(Collections.singletonMap("message", "Logout successful"));
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