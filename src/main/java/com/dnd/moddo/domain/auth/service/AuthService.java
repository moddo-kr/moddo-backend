package com.dnd.moddo.domain.auth.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.auth.dto.KakaoLogoutResponse;
import com.dnd.moddo.domain.auth.dto.KakaoProfile;
import com.dnd.moddo.domain.auth.dto.KakaoTokenResponse;
import com.dnd.moddo.domain.user.dto.request.GuestUserSaveRequest;
import com.dnd.moddo.domain.user.dto.request.UserSaveRequest;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.service.CommandUserService;
import com.dnd.moddo.domain.user.service.QueryUserService;
import com.dnd.moddo.global.exception.ModdoException;
import com.dnd.moddo.global.jwt.dto.TokenResponse;
import com.dnd.moddo.global.jwt.utill.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService {

	private final CommandUserService commandUserService;
	private final QueryUserService queryUserService;
	private final JwtProvider jwtProvider;
	private final KakaoClient kakaoClient;

	@Transactional
	public TokenResponse loginWithGuest() {
		String guestEmail = "guest-" + UUID.randomUUID() + "@guest.com";
		GuestUserSaveRequest request = new GuestUserSaveRequest(guestEmail, "Guest");

		User user = commandUserService.createGuestUser(request);

		return jwtProvider.generateToken(user);
	}

	@Transactional
	public TokenResponse loginOrRegisterWithKakao(String code) {
		KakaoTokenResponse tokenResponse = kakaoClient.join(code);
		KakaoProfile kakaoProfile = kakaoClient.getKakaoProfile(tokenResponse.accessToken());

		String email = kakaoProfile.kakaoAccount().email();
		String nickname = kakaoProfile.properties().nickname();
		Long kakaoId = kakaoProfile.id();

		if (email == null || nickname == null || kakaoId == null) {
			throw new ModdoException(HttpStatus.BAD_REQUEST, "카카오 프로필 정보가 누락되었습니다.");
		}

		UserSaveRequest request = new UserSaveRequest(email, nickname, kakaoId);
		User user = commandUserService.getOrCreateUser(request);

		log.info("[USER_LOGIN] 로그인 성공 : code = {}, kakaoId =  {}, nickname = {}", code, kakaoId, nickname);

		return jwtProvider.generateToken(user);
	}

	public void logout(Long userId) {
		queryUserService.findKakaoIdById(userId).ifPresent(kakaoId -> {
			KakaoLogoutResponse logoutResponse = kakaoClient.logout(kakaoId);

			if (!kakaoId.equals(logoutResponse.id())) {
				throw new ModdoException(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 로그아웃 실패: id 불일치");
			}

			log.info("[USER_LOGOUT] 카카오 로그아웃 성공: userId={}, kakaoId={}", userId, kakaoId);
		});
	}

}