package com.dnd.moddo.domain.reward.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.global.util.RestDocsTestSupport;
import com.dnd.moddo.reward.presentation.response.CollectionListResponse;
import com.dnd.moddo.reward.presentation.response.CollectionResponse;

public class CollectionControllerTest extends RestDocsTestSupport {

	@Test
	@DisplayName("나의 컬렉션 목록을 성공적으로 조회한다.")
	void getMyCollections() throws Exception {
		// given
		Long userId = 1L;
		CollectionListResponse response = new CollectionListResponse(List.of(
			new CollectionResponse(1L, "모또", 1, LocalDateTime.now(), "imageUrl", "imageBigUrl")
		));

		given(loginUserArgumentResolver.supportsParameter(any()))
			.willReturn(true);

		given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.willReturn(new LoginUserInfo(userId, "USER"));

		given(queryCollectionService.findCollectionListByUserId(userId)).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/v1/collections")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.collections").isArray())
			.andExpect(jsonPath("$.collections[0].id").value(1L))
			.andExpect(jsonPath("$.collections[0].name").value("모또"))
			.andDo(restDocs.document(
				responseFields(
					fieldWithPath("collections").type(JsonFieldType.ARRAY).description("보유한 캐릭터 도감 목록"),
					fieldWithPath("collections[].id").type(JsonFieldType.NUMBER).description("캐릭터 ID"),
					fieldWithPath("collections[].name").type(JsonFieldType.STRING).description("캐릭터 이름"),
					fieldWithPath("collections[].rarity").type(JsonFieldType.NUMBER).description("캐릭터 희귀도"),
					fieldWithPath("collections[].acquiredAt").type(JsonFieldType.STRING).description("획득 일시").optional(),
					fieldWithPath("collections[].imageUrl").type(JsonFieldType.STRING).description("캐릭터 이미지 URL").optional(),
					fieldWithPath("collections[].imageBigUrl").type(JsonFieldType.STRING)
						.description("캐릭터 상세 이미지 URL")
						.optional()
				)
			));
	}
}
