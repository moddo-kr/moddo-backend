package com.dnd.moddo.domain.character.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import com.dnd.moddo.domain.auth.exception.TokenInvalidException;
import com.dnd.moddo.domain.image.dto.CharacterResponse;
import com.dnd.moddo.global.jwt.exception.MissingTokenException;
import com.dnd.moddo.global.util.RestDocsTestSupport;

public class CharacterControllerTest extends RestDocsTestSupport {

	@Test
	@DisplayName("캐릭터 정보를 정상적으로 조회한다.")
	void getCharacterSuccess() throws Exception {
		// given
		String groupToken = "groupToken";
		Long groupId = 1L;

		CharacterResponse mockResponse = new CharacterResponse(
			"천사 모또", "2", "https://moddo-s3.s3.amazonaws.com/character/천사 모또-2.png",
			"https://moddo-s3.s3.amazonaws.com/character/천사 모또-2-big.png"
		);

		Mockito.when(querySettlementService.findIdByCode(groupToken)).thenReturn(groupId);
		Mockito.when(queryCharacterService.findCharacterByGroupId(eq(groupId))).thenReturn(mockResponse);

		// when & then
		mockMvc.perform(get("/api/v1/character")
				.param("groupToken", groupToken)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("천사 모또"))
			.andExpect(jsonPath("$.rarity").value("2"))
			.andExpect(jsonPath("$.imageUrl").value("https://moddo-s3.s3.amazonaws.com/character/천사 모또-2.png"))
			.andExpect(jsonPath("$.imageBigUrl").value("https://moddo-s3.s3.amazonaws.com/character/천사 모또-2-big.png"))
			.andDo(print());

		verify(querySettlementService).findIdByCode(groupToken);
		verify(queryCharacterService).findCharacterByGroupId(groupId);
	}

	@Test
	@DisplayName("유효하지 않은 groupToken일 경우 에러가 발생한다.")
	void getCharacterInvalidToken() throws Exception {
		// given
		String groupToken = "invalid.groupToken";
		when(querySettlementService.findIdByCode(groupToken)).thenThrow(new TokenInvalidException());

		// when & then
		mockMvc.perform(get("/api/v1/character")
				.param("groupToken", groupToken)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());

		verify(querySettlementService).findIdByCode(groupToken);
		verify(queryCharacterService, never()).findCharacterByGroupId(any());
	}

	@Test
	@DisplayName("groupToken이 비어있는 경우 에러가 발생한다.")
	void getCharacterMissingToken() throws Exception {
		// when
		String groupToken = "";
		when(querySettlementService.findIdByCode(groupToken)).thenThrow(new MissingTokenException());

		// then
		mockMvc.perform(get("/api/v1/character")
				.param("groupToken", groupToken)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());

		verify(querySettlementService).findIdByCode(groupToken);
		verify(queryCharacterService, never()).findCharacterByGroupId(any());
	}
}
