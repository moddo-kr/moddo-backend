package com.dnd.moddo.domain.user.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.global.util.RestDocsTestSupport;
import com.dnd.moddo.user.presentation.response.UserResponse;

public class UserControllerTest extends RestDocsTestSupport {

	@Test
	@DisplayName("내 정보를 성공적으로 조회한다.")
	void getUser() throws Exception {
		// given
		Long userId = 1L;
		UserResponse response = UserResponse.builder()
			.name("김모또")
			.email("moddo@example.com")
			.profile("https://moddo-s3.s3.amazonaws.com/profile/MODDO.png")
			.build();

		given(loginUserArgumentResolver.supportsParameter(any()))
			.willReturn(true);

		given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.willReturn(new LoginUserInfo(userId, "USER"));

		given(queryUserService.findUserById(userId)).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/v1/user")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value(response.name()))
			.andExpect(jsonPath("$.email").value(response.email()))
			.andExpect(jsonPath("$.profile").value(response.profile()));
	}
}
