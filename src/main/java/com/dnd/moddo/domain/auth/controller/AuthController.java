package com.dnd.moddo.domain.auth.controller;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.domain.auth.dto.KakaoTokenResponse;
import com.dnd.moddo.domain.auth.service.AuthService;
import com.dnd.moddo.domain.auth.service.KakaoClient;
import com.dnd.moddo.domain.auth.service.RefreshTokenService;
import com.dnd.moddo.global.jwt.dto.RefreshResponse;
import com.dnd.moddo.global.jwt.dto.TokenResponse;
import com.dnd.moddo.global.jwt.properties.CookieProperties;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@EnableConfigurationProperties(CookieProperties.class)
@RequestMapping("/api/v1")
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;
	private final KakaoClient kakaoClient;
	private final CookieProperties cookieProperties;

	/**
	 * Issues a guest user access token and sets it as an HTTP-only cookie in the response.
	 *
	 * @return a response containing the guest user's access token in the body and as a secure cookie.
	 */
	@GetMapping("/user/guest/token")
	public ResponseEntity<TokenResponse> getGuestToken() {
		TokenResponse tokenResponse = authService.createGuestUser();

		String cookie = createCookie("accessToken", tokenResponse.accessToken()).toString();

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookie)
			.body(tokenResponse);
	}

	/**
	 * Reissues an access token using the provided refresh token.
	 *
	 * @param refreshToken the refresh token from the Authorization header
	 * @return a response containing the new access token and related information
	 */
	@PutMapping("/user/reissue/token")
	public RefreshResponse reissueAccessToken(@RequestHeader(value = "Authorization") @NotBlank String refreshToken) {
		return refreshTokenService.execute(refreshToken);
	}

	/**
	 * Handles the Kakao OAuth2 login callback by exchanging the authorization code for a Kakao access token,
	 * issuing or retrieving a user access token, and setting it as an HTTP-only cookie in the response.
	 *
	 * @param code the authorization code received from Kakao OAuth2 login
	 * @return a response with the access token set as a cookie and an empty body
	 */
	@GetMapping("/login/oauth2/callback")
	public ResponseEntity<Void> kakaoLoginCallback(@RequestParam String code) {
		KakaoTokenResponse kakaoTokenResponse = kakaoClient.join(code);

		TokenResponse tokenResponse = authService.getOrCreateKakaoUserToken(kakaoTokenResponse.access_token());

		String cookie = createCookie("accessToken", tokenResponse.accessToken()).toString();

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookie)
			.build();
	}

	/**
	 * Logs out the user by expiring the "accessToken" cookie.
	 *
	 * Removes the access token from the client by setting an expired cookie in the response header.
	 * Returns an HTTP 200 OK response with no body.
	 */
	@GetMapping("/logout")
	public ResponseEntity<Void> kakaoLogout() {
		String cookie = expireCookie("accessToken").toString();

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookie)
			.build();
	}

	/**
	 * Creates an HTTP cookie with the specified name and value, applying security and configuration attributes from {@code cookieProperties}.
	 *
	 * @param name the name of the cookie
	 * @param key the value to set for the cookie
	 * @return a configured {@link ResponseCookie} instance
	 */
	private ResponseCookie createCookie(String name, String key) {
		return ResponseCookie.from(name, key)
			.httpOnly(cookieProperties.httpOnly())
			.secure(cookieProperties.secure())
			.path(cookieProperties.path())
			.domain(cookieProperties.domain())
			.sameSite(cookieProperties.sameSite())
			.maxAge(cookieProperties.maxAge())
			.build();

	}

	/**
	 * Creates a ResponseCookie with the specified name that is immediately expired.
	 *
	 * The cookie is configured with security and path attributes from the application's cookie properties,
	 * and its max age is set to zero to instruct the client to remove it.
	 *
	 * @param name the name of the cookie to expire
	 * @return a ResponseCookie configured to expire immediately
	 */
	private ResponseCookie expireCookie(String name) {
		return ResponseCookie.from(name, null)
			.httpOnly(cookieProperties.httpOnly())
			.secure(cookieProperties.secure())
			.path(cookieProperties.path())
			.domain(cookieProperties.domain())
			.sameSite(cookieProperties.sameSite())
			.maxAge(0L)
			.build();
	}
}