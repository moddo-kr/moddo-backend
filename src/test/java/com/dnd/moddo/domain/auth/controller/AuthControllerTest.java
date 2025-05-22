package com.dnd.moddo.domain.auth.controller;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.dnd.moddo.domain.auth.service.AuthService;
import com.dnd.moddo.domain.auth.service.RefreshTokenService;
import com.dnd.moddo.global.jwt.dto.RefreshResponse;
import com.dnd.moddo.global.jwt.dto.TokenResponse;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(AuthController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@MockBean
	private RefreshTokenService refreshTokenService;

	@BeforeEach
	void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration(
				restDocumentation))
			.alwaysDo(print())
			.build();
	}

	@Test
	void getGuestToken() throws Exception {
		when(authService.createGuestUser()).thenReturn(
			new TokenResponse("access-token", "refresh-token", ZonedDateTime.now(), false)
		);

		mockMvc.perform(get("/api/v1/user/guest/token")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("auth-get-guest-token",
				responseFields(
					fieldWithPath("accessToken").type(JsonFieldType.STRING).description("발급된 Access Token"),
					fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("발급된 Refresh Token")
				)
			));
	}

	@Test
	void reissueAccessToken() throws Exception {
		when(refreshTokenService.execute("Bearer refresh-token")).thenReturn(
			RefreshResponse.builder().accessToken("new-access-token").build()
		);

		mockMvc.perform(put("/api/v1/user/reissue/token")
				.header("Authorization", "Bearer refresh-token")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("auth-put-reissue-token",
				requestHeaders(
					headerWithName("Authorization").description("기존 Refresh Token")
				),
				responseFields(
					fieldWithPath("accessToken").type(JsonFieldType.STRING).description("새로 발급된 Access Token")
				)
			));
	}
}
