package com.dnd.moddo.domain.auth.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import com.dnd.moddo.domain.auth.service.AuthService;
import com.dnd.moddo.domain.auth.service.RefreshTokenService;
import com.dnd.moddo.global.jwt.dto.RefreshResponse;
import com.dnd.moddo.global.jwt.dto.TokenResponse;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@MockBean
	private RefreshTokenService refreshTokenService;

	@Test
	@DisplayName("게스트 토큰 발급")
	void getGuestToken() throws Exception {
		TokenResponse response = new TokenResponse("access-token", "refresh-token", ZonedDateTime.now().plusDays(30),
			false);
		Mockito.when(authService.createGuestUser()).thenReturn(response);

		mockMvc.perform(get("/api/v1/user/guest/token"))
			.andExpect(status().isOk())
			.andDo(document("auth-get-guest-token",
				responseFields(
					fieldWithPath("accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
					fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
					fieldWithPath("expiredAt").type(JsonFieldType.STRING).description("리프레시 토큰 만료 시간"),
					fieldWithPath("isMember").type(JsonFieldType.BOOLEAN).description("회원 여부")
				)
			));
	}

	@Test
	@DisplayName("엑세스 토큰 재발급")
	void reissueAccessToken() throws Exception {
		RefreshResponse response = RefreshResponse.builder()
			.accessToken("new-access-token")
			.build();

		Mockito.when(refreshTokenService.execute(anyString())).thenReturn(response);

		mockMvc.perform(put("/api/v1/user/reissue/token")
				.header("Authorization", "Bearer refresh-token"))
			.andExpect(status().isOk())
			.andDo(document("auth-reissue-token",
				requestHeaders(
					headerWithName("Authorization").description("리프레시 토큰")
				),
				responseFields(
					fieldWithPath("accessToken").type(JsonFieldType.STRING).description("새 액세스 토큰")
				)
			));
	}
}
