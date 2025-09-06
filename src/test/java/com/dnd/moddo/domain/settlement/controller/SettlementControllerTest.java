package com.dnd.moddo.domain.settlement.controller;

import static com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMemberResponse;
import com.dnd.moddo.domain.settlement.dto.request.SettlementAccountRequest;
import com.dnd.moddo.domain.settlement.dto.request.SettlementPasswordRequest;
import com.dnd.moddo.domain.settlement.dto.request.SettlementRequest;
import com.dnd.moddo.domain.settlement.dto.response.SettlementDetailResponse;
import com.dnd.moddo.domain.settlement.dto.response.SettlementHeaderResponse;
import com.dnd.moddo.domain.settlement.dto.response.SettlementPasswordResponse;
import com.dnd.moddo.domain.settlement.dto.response.SettlementResponse;
import com.dnd.moddo.domain.settlement.dto.response.SettlementSaveResponse;
import com.dnd.moddo.global.util.RestDocsTestSupport;

import jakarta.servlet.http.HttpServletRequest;

public class SettlementControllerTest extends RestDocsTestSupport {

	@Test
	@DisplayName("모임을 성공적으로 생성한다.")
	void saveSettlement() throws Exception {
		// given
		SettlementRequest request = new SettlementRequest("모또 모임", "1234");
		SettlementSaveResponse response = new SettlementSaveResponse("groupToken", new AppointmentMemberResponse(
			1L, MANAGER, "김모또", "https://moddo-s3.s3.amazonaws.com/profile/MODDO.png", true, LocalDateTime.now()
		));

		given(jwtService.getUserId(any(HttpServletRequest.class))).willReturn(1L);
		given(commandSettlementService.createSettlement(any(), eq(1L))).willReturn(response);

		// when & then
		mockMvc.perform(post("/api/v1/group")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.groupToken").value("groupToken"));
	}

	@Test
	@DisplayName("계좌 정보를 성공적으로 수정한다.")
	void updateAccount() throws Exception {
		// given
		SettlementAccountRequest accountRequest = new SettlementAccountRequest("우리은행", "1111-1111");
		SettlementResponse response = new SettlementResponse(
			1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusMonths(1), "우리은행", "1111-1111",
			LocalDateTime.now().plusDays(1)
		);

		given(jwtService.getUserId(any(HttpServletRequest.class))).willReturn(1L);
		given(querySettlementService.findIdByCode(anyString())).willReturn(100L);
		given(commandSettlementService.updateAccount(any(), eq(1L), eq(100L))).willReturn(response);

		// when & then
		mockMvc.perform(put("/api/v1/group/account")
				.param("groupToken", "groupToken")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(accountRequest)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("모임을 성공적으로 조회한다.")
	void getSettlement() throws Exception {
		// given
		SettlementDetailResponse response = new SettlementDetailResponse(1L, "모또 모임", List.of(
			new AppointmentMemberResponse(1L, MANAGER, "김모또", "https://moddo-s3.s3.amazonaws.com/profile/MODDO.png",
				true,
				LocalDateTime.now())
		));

		given(jwtService.getUserId(any(HttpServletRequest.class))).willReturn(1L);
		given(querySettlementService.findIdByCode(anyString())).willReturn(100L);
		given(querySettlementService.findOne(100L, 1L)).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/v1/group")
				.param("groupToken", "groupToken"))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("비밀번호를 성공적으로 검증한다.")
	void isPasswordMatch() throws Exception {
		// given
		SettlementPasswordRequest request = new SettlementPasswordRequest("1234");
		SettlementPasswordResponse response = SettlementPasswordResponse.from("확인되었습니다.");

		given(jwtService.getUserId(any(HttpServletRequest.class))).willReturn(1L);
		given(querySettlementService.findIdByCode(anyString())).willReturn(100L);
		given(commandSettlementService.isPasswordMatch(100L, 1L, request)).willReturn(response);

		// when & then
		mockMvc.perform(post("/api/v1/group/password")
				.param("groupToken", "groupToken")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("확인되었습니다."));
	}

	@Test
	@DisplayName("모임의 헤더를 성공적으로 조회한다.")
	void getHeader() throws Exception {
		// given
		SettlementHeaderResponse response = SettlementHeaderResponse.of("모또 모임", 10000L,
			LocalDateTime.now().plusDays(1), "우리은행",
			"1111-1111");

		given(querySettlementService.findIdByCode("groupToken")).willReturn(100L);
		given(querySettlementService.findBySettlementHeader(100L)).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/v1/group/header")
				.param("groupToken", "groupToken"))
			.andExpect(status().isOk());
	}
}
