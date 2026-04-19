package com.dnd.moddo.auth.application;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.dnd.moddo.auth.presentation.response.KakaoLogoutResponse;
import com.dnd.moddo.auth.presentation.response.KakaoProfile;
import com.dnd.moddo.auth.presentation.response.KakaoTokenResponse;
import com.dnd.moddo.common.config.KakaoProperties;
import com.dnd.moddo.common.exception.ModdoException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KakaoClient {
	private final KakaoProperties kakaoProperties;
	private final RestClient restClient;

	public KakaoClient(KakaoProperties kakaoProperties, RestClient.Builder builder) {
		this.kakaoProperties = kakaoProperties;
		this.restClient = builder.build();
	}

	public KakaoTokenResponse join(String code, String state) {
		String uri = kakaoProperties.tokenRequestUri();
		String redirectUri = resolveRedirectUri(state);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", kakaoProperties.clientId());
		params.add("redirect_uri", redirectUri);
		params.add("code", code);

		try {
			return restClient.post()
				.uri(uri)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(params)
				.retrieve()
				.body(KakaoTokenResponse.class);

		} catch (RestClientResponseException e) {
			log.error("[KAKAO_API][GET_PROFILE][HTTP_ERROR] HTTP 에러 발생: status={}, body={}, error={}",
				e.getStatusCode(), e.getResponseBodyAsString(), e.getMessage());
			throw new ModdoException(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 API HTTP 에러");
		} catch (Exception e) {
			log.info("[USER_LOGIN_FAIL] 로그인 실패 : code = {}", code);
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private String resolveRedirectUri(String state) {
		String localRedirectUri = kakaoProperties.localRedirectUri();
		if (state == null || localRedirectUri == null) {
			return kakaoProperties.redirectUri();
		}

		try {
			URI stateUri = URI.create(state);
			URI localUri = URI.create(localRedirectUri);

			if (isSameOrigin(stateUri, localUri)) {
				return localRedirectUri;
			}
		} catch (IllegalArgumentException e) {
			log.warn("[KAKAO_LOGIN] state 파싱 실패, 기본 redirectUri 사용: state={}", state);
		}

		return kakaoProperties.redirectUri();
	}

	private boolean isSameOrigin(URI sourceUri, URI targetUri) {
		return sourceUri.isAbsolute()
			&& targetUri.isAbsolute()
			&& sourceUri.getScheme() != null
			&& targetUri.getScheme() != null
			&& sourceUri.getHost() != null
			&& targetUri.getHost() != null
			&& sourceUri.getScheme().equalsIgnoreCase(targetUri.getScheme())
			&& sourceUri.getHost().equalsIgnoreCase(targetUri.getHost())
			&& sourceUri.getPort() == targetUri.getPort();
	}

	public KakaoProfile getKakaoProfile(String token) {
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

	public KakaoLogoutResponse unlink(Long kakaoId) {
		String uri = kakaoProperties.unlinkRequestUri();

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
