package com.dnd.moddo.domain.auth.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import com.dnd.moddo.domain.auth.dto.KakaoTokenResponse;
import com.dnd.moddo.global.jwt.dto.RefreshResponse;
import com.dnd.moddo.global.jwt.dto.TokenResponse;
import com.dnd.moddo.global.util.RestDocsTestSupport;

class AuthControllerTest extends RestDocsTestSupport {

	@Test
	@DisplayName("게스트 토큰을 성공적으로 발급한다.")
	void getGuestToken() throws Exception {
		// given
		TokenResponse response = new TokenResponse(
			"access-token",
			"refresh-token",
			ZonedDateTime.now().plusDays(30),
			false
		);
		given(authService.loginWithGuest()).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/v1/user/guest/token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").value("access-token"))
			.andExpect(jsonPath("$.refreshToken").value("refresh-token"))
			.andExpect(jsonPath("$.isMember").value(false))
			.andDo(restDocs.document(
				responseHeaders(
					headerWithName("Set-Cookie").description("엑세스 토큰")
				),
				responseFields(
					fieldWithPath("accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
					fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
					fieldWithPath("expiredAt").type(JsonFieldType.STRING).description("리프레시 토큰 만료 시간"),
					fieldWithPath("isMember").type(JsonFieldType.BOOLEAN).description("회원 여부")
				)
			));
	}

	@Test
	@DisplayName("리프레시 토큰으로 액세스 토큰을 성공적으로 재발급한다.")
	void reissueAccessToken() throws Exception {
		// given
		RefreshResponse response = RefreshResponse.builder()
			.accessToken("new-access-token")
			.build();
		given(refreshTokenService.execute(anyString())).willReturn(response);

		// when & then
		mockMvc.perform(put("/api/v1/user/reissue/token")
				.header("Authorization", "Bearer refresh-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").value("new-access-token"))
			.andDo(restDocs.document(
				requestHeaders(
					headerWithName("Authorization").description("리프레시 토큰")
				),
				responseFields(
					fieldWithPath("accessToken").type(JsonFieldType.STRING).description("새 액세스 토큰")
				)
			));
	}

	@Test
	@DisplayName("카카오에서 인가코드를 통해 토큰을 발급받아 사용자 정보를 가져와 등록시킨 뒤 엑세스 토큰을 발급하여 쿠키로 전달한다.")
	void kakaoLoginCallback() throws Exception {
		//given
		KakaoTokenResponse kakaoTokenResponse = new KakaoTokenResponse("kakao-access-token", 3600);
		given(kakaoClient.join(anyString())).willReturn(kakaoTokenResponse);

		TokenResponse tokenResponse = new TokenResponse("access-token", "refresh-token",
			ZonedDateTime.now().plusMonths(1), true);
		given(authService.loginOrRegisterWithKakao(anyString())).willReturn(tokenResponse);

		//when & then
		mockMvc.perform(get("/api/v1/login/oauth2/callback")
				.param("code", "test code"))
			.andExpect(status().isOk())
			.andDo(restDocs.document(
				queryParameters(
					parameterWithName("code").description("카카오 인가 코드")
				),
				responseHeaders(
					headerWithName("Set-Cookie").description("엑세스 토큰")
				)
			));
	}
}
