package com.dnd.moddo.auth.presentation;

import static com.dnd.moddo.auth.infrastructure.security.JwtConstants.*;

import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.auth.application.AuthService;
import com.dnd.moddo.auth.application.RefreshTokenService;
import com.dnd.moddo.auth.infrastructure.security.JwtProvider;
import com.dnd.moddo.auth.infrastructure.security.LoginUser;
import com.dnd.moddo.auth.presentation.response.AuthCheckResponse;
import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.auth.presentation.response.TokenResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1")
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;
	private final JwtProvider jwtProvider;
	private final AuthCookieManager authCookieManager;
	private final AuthRedirectResolver authRedirectResolver;

	@GetMapping("/user/guest/token")
	public ResponseEntity<Void> getGuestToken() {
		TokenResponse tokenResponse = authService.loginWithGuest();

		String accessTokenCookie = authCookieManager.createCookie("accessToken", tokenResponse.accessToken());
		String refreshTokenCookie = authCookieManager.createCookie("refreshToken", tokenResponse.refreshToken());

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, accessTokenCookie, refreshTokenCookie)
			.build();
	}

	@PutMapping("/user/reissue/token")
	public ResponseEntity<Void> reissueAccessToken(
		@CookieValue(value = "refreshToken") @NotBlank String refreshToken
	) {
		String accessToken = refreshTokenService.execute(refreshToken).getAccessToken();
		String accessTokenCookie = authCookieManager.createCookie("accessToken", accessToken);

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, accessTokenCookie)
			.build();
	}

	@GetMapping("/login/oauth2/callback")
	public ResponseEntity<Void> kakaoLoginCallback(@RequestParam @NotBlank String code,
		@RequestParam @NotBlank String state,
		HttpServletRequest request) {

		TokenResponse tokenResponse = authService.loginOrRegisterWithKakao(code, state);
		String redirectUrl = authRedirectResolver.resolve(state, request);

		String accessTokenCookie = authCookieManager.createCookie("accessToken", tokenResponse.accessToken());
		String refreshTokenCookie = authCookieManager.createCookie("refreshToken", tokenResponse.refreshToken());

		return ResponseEntity.status(HttpStatus.FOUND)
			.header(HttpHeaders.LOCATION, redirectUrl)
			.header(HttpHeaders.SET_COOKIE, accessTokenCookie, refreshTokenCookie)
			.build();
	}

	@PostMapping("/logout")
	public ResponseEntity<?> kakaoLogout(@CookieValue(value = "accessToken") String token,
		@LoginUser LoginUserInfo loginUser) {
		String accessTokenCookie = authCookieManager.expireCookie("accessToken");
		String refreshTokenCookie = authCookieManager.expireCookie("refreshToken");
		authService.logout(loginUser.userId());
		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, accessTokenCookie, refreshTokenCookie)
			.body(Collections.singletonMap("message", "Logout successful"));
	}

	@DeleteMapping("/unlink")
	public ResponseEntity<?> kakaoUnlink(@CookieValue(value = "accessToken") String token,
		@LoginUser LoginUserInfo loginUser) {
		String accessTokenCookie = authCookieManager.expireCookie("accessToken");
		String refreshTokenCookie = authCookieManager.expireCookie("refreshToken");
		authService.unlink(loginUser.userId());
		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, accessTokenCookie, refreshTokenCookie)
			.body(Collections.singletonMap("message", "Unlink successful"));
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

}
