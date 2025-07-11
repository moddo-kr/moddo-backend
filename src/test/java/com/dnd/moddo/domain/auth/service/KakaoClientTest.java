package com.dnd.moddo.domain.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.dnd.moddo.domain.auth.dto.KakaoProfile;
import com.dnd.moddo.domain.auth.dto.KakaoTokenResponse;
import com.dnd.moddo.global.config.KakaoProperties;
import com.dnd.moddo.global.exception.ModdoException;

@ExtendWith(SpringExtension.class)
@RestClientTest(value = KakaoClient.class)
@EnableConfigurationProperties(KakaoProperties.class)
public class KakaoClientTest {
	@Autowired
	private KakaoClient kakaoClient;

	@Autowired
	private MockRestServiceServer mockServer;

	@Autowired
	private KakaoProperties kakaoProperties;

	@DisplayName("카카오 인가 코드로 토큰 요청하면 OauthToken을 반환한다")
	@Test
	void whenRequestKakaoAccessToken_thenReturnOauthToken() throws Exception {
		// given
		String code = "test_code";

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("code", "test_code");
		params.add("grant_type", "authorization_code");
		params.add("client_id", kakaoProperties.clientId());
		params.add("redirect_uri", kakaoProperties.redirectUri());

		String expectResponse = """
			{
			  "access_token": "test_token",
			  "expires_in": 3600
			}
			""";

		mockServer.expect(requestTo(kakaoProperties.tokenRequestUri()))
			.andExpect(method(HttpMethod.POST))
			.andExpect(header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"))
			.andExpect(content().formData(params))
			.andRespond(withSuccess(expectResponse, MediaType.APPLICATION_JSON));
		// when
		KakaoTokenResponse result = kakaoClient.join("test_code");

		// then
		assertThat(result).isNotNull();
		assertThat(result.accessToken()).isEqualTo("test_token");
	}

	@DisplayName("잘못된 인가 코드로 토큰 요청 시 IllegalArgumentException이 발생한다")
	@Test
	void whenRequestKakaoAccessTokenWithInvalidCode_thenThrowException() {
		//given
		String code = "invalid_code";

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", kakaoProperties.clientId());
		params.add("redirect_uri", kakaoProperties.redirectUri());
		params.add("code", "invalid_code");

		mockServer.expect(requestTo(kakaoProperties.tokenRequestUri()))
			.andExpect(method(HttpMethod.POST))
			.andExpect(header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"))
			.andExpect(content().formData(params))
			.andRespond(withStatus(HttpStatus.BAD_REQUEST));

		//when & then
		assertThatThrownBy(() -> kakaoClient.join(code))
			.isInstanceOf(ModdoException.class)
			.hasMessageContaining("카카오 API HTTP 에러");
	}

	@DisplayName("정상 토큰으로 카카오 프로필 요청 시 KakaoProfile이 반환된다")
	@Test
	void whenGetKakaoProfile_thenReturnKakaoProfile() {
		// given
		String token = "test_token";

		String expectResponse = """
			{
			  "id": 12345,
			  "properties": {
				"nickname": "테스트유저"
			  },
			  "kakao_account": {
			  	"email": "test@example.com",
				"profile": {
				  "nickname": "테스트 유저"
				}
			  }
			}
			""";

		mockServer.expect(requestTo(kakaoProperties.profileRequestUri()))
			.andExpect(method(HttpMethod.GET))
			.andExpect(header("Authorization", "Bearer " + token))
			.andRespond(withSuccess(expectResponse, MediaType.APPLICATION_JSON));

		// when
		KakaoProfile profile = kakaoClient.getKakaoProfile(token);

		// then
		assertThat(profile).isNotNull();
		assertThat(profile.id()).isEqualTo(12345L);
		assertThat(profile.kakaoAccount().email()).isEqualTo("test@example.com");
		assertThat(profile.properties().nickname()).isEqualTo("테스트유저");
	}

	@DisplayName("카카오 API에서 에러가 발생하면 IllegalArgumentException이 발생한다")
	@Test
	void whenGetKakaoProfileWithHttpError_thenThrowException() {
		// given
		String token = "test_token";

		mockServer.expect(requestTo(kakaoProperties.profileRequestUri()))
			.andExpect(method(HttpMethod.GET))
			.andExpect(header("Authorization", "Bearer " + token))
			.andRespond(withServerError());

		// when & then
		assertThatThrownBy(() -> kakaoClient.getKakaoProfile(token))
			.isInstanceOf(ModdoException.class);
	}
}
