package com.dnd.moddo.domain.auth.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dnd.moddo.domain.auth.service.AuthService;
import com.dnd.moddo.domain.auth.service.RefreshTokenService;
import com.dnd.moddo.global.jwt.dto.RefreshResponse;
import com.dnd.moddo.global.jwt.dto.TokenResponse;
import com.dnd.moddo.global.util.RestDocsTestSupport;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(AuthController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
class AuthControllerTest extends RestDocsTestSupport {

	@MockBean
	private AuthService authService;

	@MockBean
	private RefreshTokenService refreshTokenService;

	@Test
	void get_geustToken() throws Exception {
		TokenResponse response = new TokenResponse("access-token", "refresh-token", ZonedDateTime.now(), false);

		given(authService.createGuestUser()).willReturn(response);

		mockMvc.perform(get("/api/v1/user/guest/token")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(restDocs.document(
				responseFields(
					fieldWithPath("accessToken").type(JsonFieldType.STRING).description("Access Token"),
					fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("Refresh Token")
				)
			));
	}

	@Test
	void reissue_accessToken() throws Exception {
		given(refreshTokenService.execute("Bearer refresh-token"))
			.willReturn(RefreshResponse.builder().accessToken("new-access-token").build());

		mockMvc.perform(put("/api/v1/user/reissue/token")
				.header("Authorization", "Bearer refresh-token")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(restDocs.document(
				requestHeaders(
					headerWithName("Authorization").description("Bearer Refresh Token")
				),
				responseFields(
					fieldWithPath("accessToken").type(JsonFieldType.STRING).description("새로 발급된 Access Token")
				)
			));
	}

	@Test
	void notfound_refreshToken_exception() throws Exception {
		mockMvc.perform(put("/api/v1/user/reissue/token")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andDo(restDocs.document());

		Mockito.verify(refreshTokenService, never()).execute(anyString());
	}

	@Test
	void Invalid_refreshToken_exception() throws Exception {
		doThrow(new IllegalArgumentException("유효하지 않은 토큰")).when(refreshTokenService).execute("Invalid");

		mockMvc.perform(put("/api/v1/user/reissue/token")
				.header("Authorization", "Invalid")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isInternalServerError())
			.andDo(restDocs.document());
	}
}
