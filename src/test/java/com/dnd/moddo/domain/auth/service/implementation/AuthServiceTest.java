package com.dnd.moddo.domain.auth.service.implementation;

import static com.dnd.moddo.global.support.UserTestFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.dnd.moddo.auth.application.AuthService;
import com.dnd.moddo.auth.application.KakaoClient;
import com.dnd.moddo.auth.infrastructure.security.JwtProvider;
import com.dnd.moddo.auth.presentation.response.KakaoLogoutResponse;
import com.dnd.moddo.auth.presentation.response.KakaoProfile;
import com.dnd.moddo.auth.presentation.response.KakaoTokenResponse;
import com.dnd.moddo.auth.presentation.response.TokenResponse;
import com.dnd.moddo.common.exception.ModdoException;
import com.dnd.moddo.user.application.CommandUserService;
import com.dnd.moddo.user.application.QueryUserService;
import com.dnd.moddo.user.domain.User;
import com.dnd.moddo.user.presentation.request.GuestUserSaveRequest;
import com.dnd.moddo.user.presentation.request.UserSaveRequest;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@Mock
	private CommandUserService commandUserService;

	@Mock
	private QueryUserService queryUserService;

	@Mock
	private JwtProvider jwtProvider;

	@Mock
	private KakaoClient kakaoClient;

	@InjectMocks
	private AuthService authService;

	@DisplayName("게스트 로그인을 성공한다")
	@Test
	void loginWithGuest_success() {
		// given
		User user = createWithEmail("guest-uuid@guest.com");
		TokenResponse tokenResponse = new TokenResponse("access", "refresh", ZonedDateTime.now(), false);

		when(commandUserService.createGuestUser(any(GuestUserSaveRequest.class))).thenReturn(user);
		when(jwtProvider.generateToken(user)).thenReturn(tokenResponse);

		// when
		TokenResponse result = authService.loginWithGuest();

		// then
		assertThat(result).isEqualTo(tokenResponse);
		verify(commandUserService).createGuestUser(any(GuestUserSaveRequest.class));
		verify(jwtProvider).generateToken(user);
	}

	@DisplayName("카카오 로그인 또는 회원가입을 성공한다")
	@Test
	void loginOrRegisterWithKakao_success() {
		// given
		String code = "kakao_code";
		KakaoTokenResponse kakaoToken = new KakaoTokenResponse("kakao_access", 3600);
		KakaoProfile kakaoProfile = new KakaoProfile(
			12345L,
			new KakaoProfile.KakaoAccount("test@kakao.com", new KakaoProfile.Profile("nickname")),
			new KakaoProfile.Properties("nickname")
		);
		User user = createWithEmail("test@kakao.com");
		TokenResponse tokenResponse = new TokenResponse("access", "refresh", ZonedDateTime.now(), true);

		when(kakaoClient.join(code)).thenReturn(kakaoToken);
		when(kakaoClient.getKakaoProfile(kakaoToken.accessToken())).thenReturn(kakaoProfile);
		when(commandUserService.getOrCreateUser(any(UserSaveRequest.class))).thenReturn(user);
		when(jwtProvider.generateToken(user)).thenReturn(tokenResponse);

		// when
		TokenResponse result = authService.loginOrRegisterWithKakao(code);

		// then
		assertThat(result).isEqualTo(tokenResponse);
		verify(kakaoClient).join(code);
		verify(kakaoClient).getKakaoProfile(kakaoToken.accessToken());
		verify(commandUserService).getOrCreateUser(any(UserSaveRequest.class));
		verify(jwtProvider).generateToken(user);
	}

	@DisplayName("카카오 프로필 정보가 누락되면 예외가 발생한다")
	@Test
	void loginOrRegisterWithKakao_fail_profileMissing() {
		// given
		String code = "kakao_code";
		KakaoTokenResponse kakaoToken = new KakaoTokenResponse("kakao_access", 3600);
		// kakaoAccount가 null인 경우 NPE가 먼저 발생하므로, email, nickname, kakaoId 중 하나가 null인 상황을 테스트해야 함
		KakaoProfile kakaoProfile = new KakaoProfile(
			null,
			new KakaoProfile.KakaoAccount(null, null),
			new KakaoProfile.Properties(null)
		);

		when(kakaoClient.join(code)).thenReturn(kakaoToken);
		when(kakaoClient.getKakaoProfile(kakaoToken.accessToken())).thenReturn(kakaoProfile);

		// when & then
		assertThatThrownBy(() -> authService.loginOrRegisterWithKakao(code))
			.isInstanceOf(ModdoException.class)
			.hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
	}

	@DisplayName("로그아웃을 성공한다")
	@Test
	void logout_success() {
		// given
		Long userId = 1L;
		Long kakaoId = 12345L;
		when(queryUserService.findKakaoIdById(userId)).thenReturn(Optional.of(kakaoId));
		when(kakaoClient.logout(kakaoId)).thenReturn(new KakaoLogoutResponse(kakaoId));

		// when
		authService.logout(userId);

		// then
		verify(queryUserService).findKakaoIdById(userId);
		verify(kakaoClient).logout(kakaoId);
	}

	@DisplayName("카카오 로그아웃 시 ID가 일치하지 않으면 예외가 발생한다")
	@Test
	void logout_fail_idMismatch() {
		// given
		Long userId = 1L;
		Long kakaoId = 12345L;
		when(queryUserService.findKakaoIdById(userId)).thenReturn(Optional.of(kakaoId));
		when(kakaoClient.logout(kakaoId)).thenReturn(new KakaoLogoutResponse(99999L));

		// when & then
		assertThatThrownBy(() -> authService.logout(userId))
			.isInstanceOf(ModdoException.class)
			.hasFieldOrPropertyWithValue("status", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@DisplayName("연결 해제 및 회원 탈퇴를 성공한다")
	@Test
	void unlink_success() {
		// given
		Long userId = 1L;
		Long kakaoId = 12345L;
		when(queryUserService.findKakaoIdById(userId)).thenReturn(Optional.of(kakaoId));
		when(kakaoClient.unlink(kakaoId)).thenReturn(new KakaoLogoutResponse(kakaoId));

		// when
		authService.unlink(userId);

		// then
		verify(queryUserService).findKakaoIdById(userId);
		verify(kakaoClient).unlink(kakaoId);
		verify(commandUserService).deleteUser(userId);
	}

	@DisplayName("카카오 연결 해제 시 ID가 일치하지 않으면 예외가 발생한다")
	@Test
	void unlink_fail_idMismatch() {
		// given
		Long userId = 1L;
		Long kakaoId = 12345L;
		when(queryUserService.findKakaoIdById(userId)).thenReturn(Optional.of(kakaoId));
		when(kakaoClient.unlink(kakaoId)).thenReturn(new KakaoLogoutResponse(99999L));

		// when & then
		assertThatThrownBy(() -> authService.unlink(userId))
			.isInstanceOf(ModdoException.class)
			.hasFieldOrPropertyWithValue("status", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
