package com.dnd.moddo.domain.auth.service;

import static com.dnd.moddo.global.support.UserTestFactory.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.auth.dto.KakaoProfile;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;
import com.dnd.moddo.global.jwt.dto.TokenResponse;
import com.dnd.moddo.global.jwt.utill.JwtProvider;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private JwtProvider jwtProvider;
	@Mock
	private KakaoClient kakaoClient;
	@InjectMocks
	private AuthService authService;

	@DisplayName("게스트 회원을 생성하면 저장되고 토큰이 발급된다")
	@Test
	void whenCreateGuestUser_thenSaveAndIssueToken() {
		//given
		User user = createGuestDefault();
		when(userRepository.save(any(User.class))).thenReturn(user);
		//when
		TokenResponse response = authService.loginWithGuest();
		//then
		verify(userRepository, times(1)).save(any(User.class));
	}

	@DisplayName("기존 카카오 사용자가 로그인하면 토큰을 발급한다")
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
		String email = kakaoProfile.kakaoAccount().email();
		User user = createWithEmail(email);
		when(kakaoClient.getKakaoProfile(anyString())).thenReturn(kakaoProfile);
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

		//when
		TokenResponse response = authService.loginOrRegisterWithKakao(token);

		//then
		verify(jwtProvider, times(1)).generateToken(any(), anyString(), anyString(), anyBoolean());
	}

	@DisplayName("신규 카카오 사용자가 로그인하면 회원가입 후 토큰을 발급한다")
	@Test
	void whenNewKakaoUser_thenRegisterAndIssueToken() {
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
		String email = kakaoProfile.kakaoAccount().email();
		User user = createWithEmail(email);

		when(kakaoClient.getKakaoProfile(anyString())).thenReturn(kakaoProfile);
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		when(userRepository.save(any(User.class))).thenReturn(user);

		//when
		TokenResponse response = authService.loginOrRegisterWithKakao(token);

		//then
		verify(userRepository, times(1)).save(any(User.class));
		verify(jwtProvider, times(1)).generateToken(any(), anyString(), anyString(), anyBoolean());
	}
}
