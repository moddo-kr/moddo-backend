package com.dnd.moddo.domain.auth.service;

import static com.dnd.moddo.global.support.UserTestFactory.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.auth.dto.KakaoLogoutResponse;
import com.dnd.moddo.domain.auth.dto.KakaoProfile;
import com.dnd.moddo.domain.auth.dto.KakaoTokenResponse;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.service.CommandUserService;
import com.dnd.moddo.domain.user.service.QueryUserService;
import com.dnd.moddo.global.jwt.dto.TokenResponse;
import com.dnd.moddo.global.jwt.utill.JwtProvider;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
	@Mock
	private JwtProvider jwtProvider;
	@Mock
	private CommandUserService commandUserService;
	@Mock
	private QueryUserService queryUserService;
	@Mock
	private KakaoClient kakaoClient;
	@InjectMocks
	private AuthService authService;

	@DisplayName("게스트 회원을 생성하면 저장되고 토큰이 발급된다")
	@Test
	void whenCreateGuestUser_thenSaveAndIssueToken() {
		//given
		User user = createGuestDefault();
		when(commandUserService.createGuestUser(any())).thenReturn(user);
		//when
		TokenResponse response = authService.loginWithGuest();
		//then
		verify(jwtProvider, times(1)).generateToken(any());
		verify(commandUserService, times(1)).createGuestUser(any());
	}

	@DisplayName("카카오 사용자가 로그인하면 토큰을 발급한다")
	@Test
	void whenKakaoUserExists_thenTokenIsIssued() {
		//given
		String token = "test_token";
		KakaoProfile kakaoProfile = new KakaoProfile(
			12345L,
			new KakaoProfile.KakaoAccount(
				"test@example.com",
				new KakaoProfile.Profile(
					"테스트 유저"
				)
			),
			new KakaoProfile.Properties(
				"테스트유저"
			)
		);
		KakaoTokenResponse kakaoTokenResponse = new KakaoTokenResponse("access-token", 3600);
		String email = kakaoProfile.kakaoAccount().email();
		User user = createWithEmail(email);

		when(kakaoClient.join(anyString())).thenReturn(kakaoTokenResponse);
		when(kakaoClient.getKakaoProfile(anyString())).thenReturn(kakaoProfile);
		when(commandUserService.getOrCreateUser(any())).thenReturn(user);

		//when
		TokenResponse response = authService.loginOrRegisterWithKakao(token);

		//then
		verify(jwtProvider, times(1)).generateToken(any());
	}

	@DisplayName("카카오ID와 응답ID가 같을 때 카카오 로그아웃 성공한다.")
	@Test
	void whenKakaoIdMatches_thenKakaoLogoutSuccess() {
		//given
		Long kakaoId = 123456L;
		when(queryUserService.findKakaoIdById(any())).thenReturn(Optional.of(kakaoId));
		when(kakaoClient.logout(any())).thenReturn(new KakaoLogoutResponse(kakaoId));
		//when
		authService.logout(1L);
		//then
		verify(queryUserService, times(1)).findKakaoIdById(1L);
		verify(kakaoClient, times(1)).logout(kakaoId);
	}

	@DisplayName("카카오ID가 null일 때 게스트 로그아웃 성공한다.")
	@Test
	void whenKakaoIdNull_thenNoAction() {
		//given
		when(queryUserService.findKakaoIdById(any())).thenReturn(Optional.empty());
		//when
		authService.logout(1L);
		//then
		verify(queryUserService, times(1)).findKakaoIdById(1L);
		verify(kakaoClient, times(0)).logout(any());
	}

	@DisplayName("카카오ID와 응답ID가 다를 때 예외 발생한다.")
	@Test
	void whenKakaoIdDiffers_thenThrowsException() {
		//given
		Long kakaoId = 123456L;
		when(queryUserService.findKakaoIdById(any())).thenReturn(Optional.of(kakaoId));
		when(kakaoClient.logout(any())).thenReturn(new KakaoLogoutResponse(234567L));

		//when & then
		assertThatThrownBy(() -> authService.logout(1L))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("로그아웃 실패");
	}
}
