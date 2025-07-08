package com.dnd.moddo.domain.auth.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.auth.dto.KakaoProfile;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.entity.type.Authority;
import com.dnd.moddo.domain.user.repository.UserRepository;
import com.dnd.moddo.global.jwt.dto.TokenResponse;
import com.dnd.moddo.global.jwt.utill.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final KakaoClient kakaoClient;

	@Value("${kakao.auth.client_id}")
	String client_id;

	@Value("${kakao.auth.redirect_uri}")
	String redirect_uri;

	@Transactional
	public TokenResponse createGuestUser() {
		String guestEmail = "guest-" + UUID.randomUUID() + "@guest.com";

		User guestUser = createUser(guestEmail, "Guest", false);

		return jwtProvider.generateToken(guestUser.getId(), guestUser.getEmail(), guestUser.getAuthority().toString(),
			guestUser.getIsMember());
	}

	private User createUser(String email, String name, boolean isMember) {
		User user = User.builder()
			.email(email)
			.name(name)
			.profile(null)
			.createdAt(LocalDateTime.now())
			.expiredAt(LocalDateTime.now().plusMonths(1))
			.authority(Authority.USER)
			.isMember(isMember)
			.build();

		return userRepository.save(user);
	}

	@Transactional
	public TokenResponse getOrCreateKakaoUserToken(String token) {
		KakaoProfile kakaoProfile = kakaoClient.getKakaoProfile(token);

		String email = kakaoProfile.kakao_account().email();
		String nickname = kakaoProfile.properties().nickname();

		User kakaoUser = userRepository.findByEmail(email)
			.orElseGet(() -> createUser(email, nickname, true));

		log.info("[USER_LOGIN] 로그인 성공 : email={}, name={}", kakaoUser.getEmail(), kakaoUser.getName());

		return jwtProvider.generateToken(kakaoUser.getId(), kakaoUser.getEmail(), kakaoUser.getAuthority().toString(),
			kakaoUser.getIsMember());
	}

}