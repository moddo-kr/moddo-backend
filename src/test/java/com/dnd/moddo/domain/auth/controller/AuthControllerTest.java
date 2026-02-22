package com.dnd.moddo.domain.auth.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;

import com.dnd.moddo.auth.presentation.response.KakaoTokenResponse;
import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.auth.presentation.response.RefreshResponse;
import com.dnd.moddo.auth.presentation.response.TokenResponse;
import com.dnd.moddo.common.logging.ErrorNotifier;
import com.dnd.moddo.global.util.RestDocsTestSupport;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;

class AuthControllerTest extends RestDocsTestSupport {

	@MockBean
	ErrorNotifier errorNotifier;

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
				responseCookies(
					cookieWithName("accessToken").description("엑세스 토큰")
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
			.andExpect(status().is3xxRedirection())
			.andDo(document("login",
				queryParameters(
					parameterWithName("code").description("카카오 인가 코드")
				),
				responseCookies(
					cookieWithName("accessToken").description("엑세스 토큰")
				)
			));
	}

	@Test
	@DisplayName("액세스 토큰 쿠키를 통해 카카오 로그아웃을 성공적으로 수행한다.")
	void kakaoLogout() throws Exception {
		//given
		given(loginUserArgumentResolver.supportsParameter(any()))
			.willReturn(true);

		given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.willReturn(new LoginUserInfo(1L, "USER"));

		doNothing().when(authService).logout(any());

		//when & then
		mockMvc.perform(get("/api/v1/logout")
				.cookie(new Cookie("accessToken", "access-token")))
			.andExpect(status().isOk())
			.andDo(document("logout",
				requestCookies(
					cookieWithName("accessToken").description("액세스 토큰")
				),
				responseFields(
					fieldWithPath("message").type(JsonFieldType.STRING).description("로그아웃 성공 메시지")
				)
			));
	}

	@Test
	@DisplayName("토큰이 유효하면 인증 성공 응답을 반환한다.")
	void checkAuth_Success() throws Exception {
		// given
		String token = "valid-token";

		Claims claims = mock(Claims.class);
		given(jwtProvider.parseClaims(token)).willReturn(claims);
		given(jwtProvider.getUserId(token)).willReturn(1L);
		given(jwtProvider.getRole(token)).willReturn("USER");

		// when & then
		mockMvc.perform(get("/api/v1/auth/check")
				.cookie(new Cookie("accessToken", token)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.authenticated").value(true))
			.andExpect(jsonPath("$.user.id").value(1L))
			.andExpect(jsonPath("$.user.role").value("USER"))
			.andExpect(jsonPath("$.reason").doesNotExist())
			.andDo(restDocs.document(
				responseFields(
					fieldWithPath("authenticated").description("인증 여부"),
					fieldWithPath("user").description("사용자 정보").optional(),
					fieldWithPath("user.id").description("사용자 ID").optional(),
					fieldWithPath("user.role").description("사용자 권한").optional(),
					fieldWithPath("reason").description("실패 사유 (인증 실패 시)").optional()
				)
			));
	}

	@Test
	@DisplayName("토큰이 없으면 NO_TOKEN 응답을 반환한다.")
	void checkAuth_NoToken() throws Exception {

		mockMvc.perform(get("/api/v1/auth/check"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.authenticated").value(false))
			.andExpect(jsonPath("$.reason").value("NO_TOKEN"))
			.andDo(restDocs.document(
				responseFields(
					fieldWithPath("authenticated").description("인증 여부"),
					fieldWithPath("user").description("사용자 정보").optional(),
					fieldWithPath("reason").description("실패 사유 (NO_TOKEN)")
				)
			));
	}

	@Test
	@DisplayName("토큰이 만료되면 TOKEN_EXPIRED 응답을 반환한다.")
	void checkAuth_Expired() throws Exception {
		// given
		String token = "expired-token";

		given(jwtProvider.parseClaims(token))
			.willThrow(new ExpiredJwtException(null, null, "expired"));

		mockMvc.perform(get("/api/v1/auth/check")
				.cookie(new Cookie("accessToken", token)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.authenticated").value(false))
			.andExpect(jsonPath("$.reason").value("TOKEN_EXPIRED"))
			.andDo(restDocs.document(
				responseFields(
					fieldWithPath("authenticated").description("인증 여부"),
					fieldWithPath("user").description("사용자 정보").optional(),
					fieldWithPath("reason").description("실패 사유 (TOKEN_EXPIRED)")
				)
			));
	}

	@Test
	@DisplayName("토큰이 유효하지 않으면 INVALID_TOKEN 응답을 반환한다.")
	void checkAuth_InvalidToken() throws Exception {
		// given
		String token = "invalid-token";

		given(jwtProvider.parseClaims(token))
			.willThrow(new JwtException("invalid"));

		mockMvc.perform(get("/api/v1/auth/check")
				.cookie(new Cookie("accessToken", token)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.authenticated").value(false))
			.andExpect(jsonPath("$.reason").value("INVALID_TOKEN"))
			.andDo(restDocs.document(
				responseFields(
					fieldWithPath("authenticated").description("인증 여부"),
					fieldWithPath("user").description("사용자 정보").optional(),
					fieldWithPath("reason").description("실패 사유 (INVALID_TOKEN)")
				)
			));
	}
}
