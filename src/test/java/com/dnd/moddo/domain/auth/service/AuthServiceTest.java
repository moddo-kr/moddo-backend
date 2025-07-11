package com.dnd.moddo.domain.auth.service;

import static com.dnd.moddo.global.support.UserTestFactory.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.auth.dto.KakaoProfile;
import com.dnd.moddo.domain.auth.dto.KakaoTokenResponse;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.service.CommandUserService;
import com.dnd.moddo.global.jwt.dto.TokenResponse;
import com.dnd.moddo.global.jwt.utill.JwtProvider;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
	@Mock
	private JwtProvider jwtProvider;
	@Mock
	CommandUserService commandUserService;
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
}
