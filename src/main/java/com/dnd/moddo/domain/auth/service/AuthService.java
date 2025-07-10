package com.dnd.moddo.domain.auth.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.auth.dto.KakaoProfile;
import com.dnd.moddo.domain.auth.dto.KakaoTokenResponse;
import com.dnd.moddo.domain.user.dto.request.UserCreateRequest;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.service.UserService;
import com.dnd.moddo.global.jwt.dto.TokenResponse;
import com.dnd.moddo.global.jwt.utill.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService {

	private final UserService userService;
	private final JwtProvider jwtProvider;
	private final KakaoClient kakaoClient;

	@Transactional
	public TokenResponse loginWithGuest() {
		String guestEmail = "guest-" + UUID.randomUUID() + "@guest.com";
		UserCreateRequest request = new UserCreateRequest(guestEmail, "Guest", null, false);

		User user = userService.createGuestUser(request);

		return jwtProvider.generateToken(user.getId(), user.getEmail(),
			user.getAuthority().toString(), user.getIsMember());
	}

	@Transactional
	public TokenResponse loginOrRegisterWithKakao(String code) {
		KakaoTokenResponse tokenResponse = kakaoClient.join(code);
		KakaoProfile kakaoProfile = kakaoClient.getKakaoProfile(tokenResponse.accessToken());

		String email = kakaoProfile.kakaoAccount().email();
		String nickname = kakaoProfile.properties().nickname();
		Long kakaoId = kakaoProfile.id();

		UserCreateRequest request = new UserCreateRequest(email, nickname, kakaoId, true);
		User user = userService.getOrCreateUser(request);

		log.info("[USER_LOGIN] 로그인 성공 : code = {}, kakaoId =  {}, nickname = {}", code, kakaoId, nickname);

		return jwtProvider.generateToken(user.getId(), user.getEmail(), user.getAuthority().toString(),
			user.getIsMember());
	}

}