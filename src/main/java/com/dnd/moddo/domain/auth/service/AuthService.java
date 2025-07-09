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

	/**
	 * Creates a new guest user with a unique email and returns a JWT token for authentication.
	 *
	 * The guest user is assigned the name "Guest" and is marked as a non-member.
	 *
	 * @return a JWT token containing the guest user's authentication details
	 */
	@Transactional
	public TokenResponse createGuestUser() {
		String guestEmail = "guest-" + UUID.randomUUID() + "@guest.com";

		User guestUser = createUser(guestEmail, "Guest", false);

		return jwtProvider.generateToken(guestUser.getId(), guestUser.getEmail(), guestUser.getAuthority().toString(),
			guestUser.getIsMember());
	}

	/**
	 * Creates and saves a new user with the specified email, name, and membership status.
	 *
	 * The user is assigned the USER authority, a null profile, the current time as the creation date,
	 * and an expiration date set to one month from creation.
	 *
	 * @param email the user's email address
	 * @param name the user's display name
	 * @param isMember whether the user is a registered member
	 * @return the newly created and persisted User entity
	 */
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

	/**
	 * Retrieves or creates a user based on Kakao OAuth token and returns a JWT token for authentication.
	 *
	 * If a user with the Kakao account's email does not exist, a new user is created with the Kakao profile information and marked as a member.
	 *
	 * @param token the Kakao OAuth access token
	 * @return a JWT token response containing the user's authentication details
	 */
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