package com.dnd.moddo.domain.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.dnd.moddo.domain.auth.dto.KakaoProfile;
import com.dnd.moddo.domain.auth.dto.KakaoTokenResponse;
import com.dnd.moddo.global.exception.ModdoException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class KakaoClient {

	@Value("${kakao.auth.client_id}")
	String client_id;

	@Value("${kakao.auth.redirect_uri}")
	String redirect_uri;

	private final RestClient.Builder builder;

	/**
	 * Exchanges an authorization code for a Kakao OAuth access token.
	 *
	 * @param code the authorization code received from Kakao after user authentication
	 * @return the response containing the Kakao OAuth access token and related information
	 * @throws ModdoException if an HTTP error occurs during the token request
	 * @throws IllegalArgumentException if a non-HTTP error occurs during the token request
	 */
	public KakaoTokenResponse join(String code) {
		RestClient restClient = builder.build();

		String uri = "https://kauth.kakao.com/oauth/token";

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", client_id);
		params.add("redirect_uri", redirect_uri);
		params.add("code", code);

		try {
			return restClient.post()
				.uri(uri)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
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

	/**
	 * Retrieves the Kakao user profile associated with the provided access token.
	 *
	 * @param token the OAuth access token issued by Kakao
	 * @return the user's Kakao profile information
	 * @throws ModdoException if an HTTP error occurs or the profile cannot be retrieved
	 */
	public KakaoProfile getKakaoProfile(String token) {
		RestClient restClient = builder.build();

		String uri = "https://kapi.kakao.com/v2/user/me";

		try {
			return restClient.get()
				.uri(uri)
				.header("Authorization", "Bearer " + token)
				.header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
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
}
