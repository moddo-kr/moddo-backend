package com.dnd.moddo.domain.Member.controller;

import static com.dnd.moddo.event.domain.member.ExpenseRole.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.dnd.moddo.common.logging.ErrorNotifier;
import com.dnd.moddo.event.presentation.request.MemberSaveRequest;
import com.dnd.moddo.event.presentation.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.event.presentation.response.MemberResponse;
import com.dnd.moddo.event.presentation.response.MembersResponse;
import com.dnd.moddo.global.util.RestDocsTestSupport;

public class MemberControllerTest extends RestDocsTestSupport {

	@MockBean
	ErrorNotifier errorNotifier;

	@Test
	@DisplayName("모임원을 성공적으로 조회한다.")
	void getAppointmentMembers() throws Exception {
		// given
		String code = "code";
		Long groupId = 1L;

		MembersResponse mockResponse = MembersResponse.of(Collections.emptyList());

		when(querySettlementService.findIdByCode(code)).thenReturn(groupId);
		when(queryMemberService.findAll(groupId)).thenReturn(mockResponse);

		// when & then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/groups/{code}/members", code))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.members").isArray());

		verify(querySettlementService).findIdByCode(code);
		verify(queryMemberService).findAll(groupId);
	}

	@Test
	@DisplayName("모임원을 성공적으로 추가한다.")
	void saveAppointmentMember() throws Exception {
		// given
		String code = "code";
		Long groupId = 1L;

		MemberSaveRequest request = new MemberSaveRequest("김반숙");
		MemberResponse response = new MemberResponse(1L, PARTICIPANT, "김반숙",
			"https://moddo-s3.s3.amazonaws.com/profile/1.png", 1L, false, null);

		when(querySettlementService.findIdByCode(code)).thenReturn(groupId);
		when(commandMemberService.addMember(groupId, request)).thenReturn(response);

		// when & then
		mockMvc.perform(post("/api/v1/groups/{code}/members", code)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(response.id()))
			.andExpect(jsonPath("$.role").value(response.role().name()))
			.andExpect(jsonPath("$.name").value("김반숙"))
			.andExpect(jsonPath("$.profile").value(response.profile()))
			.andExpect(jsonPath("$.userId").value(response.userId()))
			.andExpect(jsonPath("$.isPaid").value(response.isPaid()))
			.andExpect(jsonPath("$.paidAt").doesNotExist());
	}

	@Test
	@DisplayName("결제 상태를 성공적으로 변경한다.")
	void updatePaymentStatus() throws Exception {
		// given
		String code = "code";
		Long groupMemberId = 1L;

		PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest(true);
		MemberResponse response = new MemberResponse(1L, PARTICIPANT, "김반숙",
			"https://moddo-s3.s3.amazonaws.com/profile/1.png", 1L, true, LocalDateTime.now());

		when(commandMemberService.updatePaymentStatus(groupMemberId, request)).thenReturn(response);

		// when & then
		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/groups/{code}/members/{memberId}", code, groupMemberId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isPaid").value(true));
	}

	@Test
	@DisplayName("모임원을 성공적으로 삭제한다.")
	void deleteAppointmentMember() throws Exception {
		// given
		Long groupMemberId = 1L;

		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/v1/groups/{code}/members/{memberId}", "code", groupMemberId))
			.andExpect(status().isNoContent());

		// when & then
		verify(commandMemberService).delete(groupMemberId);
	}

}
