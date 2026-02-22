package com.dnd.moddo.auth.presentation;

import static com.dnd.moddo.auth.infrastructure.security.JwtConstants.*;

import java.io.IOException;
import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

import com.dnd.moddo.auth.application.AuthService;
import com.dnd.moddo.auth.application.RefreshTokenService;
import com.dnd.moddo.auth.infrastructure.security.JwtProvider;
import com.dnd.moddo.auth.infrastructure.security.LoginUser;
import com.dnd.moddo.auth.presentation.response.AuthCheckResponse;
import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.auth.presentation.response.RefreshResponse;
import com.dnd.moddo.auth.presentation.response.TokenResponse;
import com.dnd.moddo.common.config.CookieProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
	private final JwtProvider jwtProvider;

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
	public ResponseEntity<?> kakaoLoginCallback(@RequestParam @NotBlank String code,
		HttpServletResponse response) throws
		IOException {

		TokenResponse tokenResponse = authService.loginOrRegisterWithKakao(code);

		String cookie = createCookie("accessToken", tokenResponse.accessToken()).toString();
		response.addHeader("Set-Cookie", cookie);
		response.sendRedirect("https://www.moddo.kr");

		return ResponseEntity.status(HttpStatus.FOUND)
			.header(HttpHeaders.LOCATION, "https://www.moddo.kr")
			.header(HttpHeaders.SET_COOKIE, cookie)
			.build();
	}

	@GetMapping("/logout")
	public ResponseEntity<?> kakaoLogout(@CookieValue(value = "accessToken") String token,
		@LoginUser LoginUserInfo loginUser) {
		String cookie = expireCookie("accessToken").toString();
		authService.logout(loginUser.userId());
		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookie)
			.body(Collections.singletonMap("message", "Logout successful"));
	}

	@GetMapping("/auth/check")
	public ResponseEntity<AuthCheckResponse> checkAuth(
		@CookieValue(value = "accessToken", required = false) String token
	) {
		if (token == null) {
			return ResponseEntity.ok(
				AuthCheckResponse.fail(AuthCheckResponse.AuthFailReason.NO_TOKEN)
			);
		}

		try {
			Claims claims = jwtProvider.parseClaims(token);

			Long userId = claims.get(AUTH_ID.getMessage(), Long.class);
			String role = claims.get(ROLE.getMessage(), String.class);

			return ResponseEntity.ok(
				AuthCheckResponse.success(userId, role)
			);

		} catch (ExpiredJwtException e) {
			return ResponseEntity.ok(
				AuthCheckResponse.fail(AuthCheckResponse.AuthFailReason.TOKEN_EXPIRED)
			);
		} catch (JwtException e) {
			return ResponseEntity.ok(
				AuthCheckResponse.fail(AuthCheckResponse.AuthFailReason.INVALID_TOKEN)
			);
		}
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