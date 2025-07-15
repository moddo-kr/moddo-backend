package com.dnd.moddo.domain.auth.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.dnd.moddo.domain.auth.dto.KakaoLogoutResponse;
import com.dnd.moddo.domain.auth.dto.KakaoProfile;
import com.dnd.moddo.domain.auth.dto.KakaoTokenResponse;
import com.dnd.moddo.global.config.KakaoProperties;
import com.dnd.moddo.global.exception.ModdoException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class KakaoClient {
	private final KakaoProperties kakaoProperties;
	private final RestClient.Builder builder;

	public KakaoTokenResponse join(String code) {
		RestClient restClient = builder.build();

		String uri = kakaoProperties.tokenRequestUri();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", kakaoProperties.clientId());
		params.add("redirect_uri", kakaoProperties.redirectUri());
		params.add("code", code);

		try {
			return restClient.post()
				.uri(uri)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(params)
				.retrieve()
				.body(KakaoTokenResponse.class);

		} catch (RestClientResponseException e) {
			log.error("[KAKAO_API][GET_TOKEN][HTTP_ERROR] HTTP 에러 발생: status={}, body={}", e.getStatusCode(),
				e.getResponseBodyAsString());
			throw new ModdoException(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 API HTTP 에러");
		} catch (Exception e) {
			log.info("[USER_LOGIN_FAIL] 로그인 실패 : code = {}", code);
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public KakaoProfile getKakaoProfile(String token) {
		RestClient restClient = builder.build();

		String uri = kakaoProperties.profileRequestUri();

		try {
			return restClient.get()
				.uri(uri)
				.header("Authorization", "Bearer " + token)
				.retrieve()
				.body(KakaoProfile.class);

		} catch (RestClientResponseException e) {
			log.error("[KAKAO_API][GET_PROFILE][HTTP_ERROR] HTTP 에러 발생: status={}, body={}", e.getStatusCode(),
				e.getResponseBodyAsString());
			throw new ModdoException(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 API HTTP 에러");
		} catch (Exception e) {
			log.error("[KAKAO_CALLBACK_ERROR] 카카오 콜백 처리 실패", e);
			throw new ModdoException(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 콜백 처리 실패");
		}
	}

	public KakaoLogoutResponse logout(Long kakaoId) {
		RestClient restClient = builder.build();
		String uri = kakaoProperties.logoutRequestUri();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("target_id_type", "user_id");
		params.add("target_id", kakaoId.toString());

		try {
			return restClient.post()
				.uri(uri)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoProperties.adminKey())
				.body(params)
				.retrieve()
				.body(KakaoLogoutResponse.class);
		} catch (Exception e) {
			log.error("[KAKAO_CALLBACK_ERROR] 카카오 콜백 처리 실패", e.getMessage());
			throw new ModdoException(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 콜백 처리 실패");
		}
	}
}
