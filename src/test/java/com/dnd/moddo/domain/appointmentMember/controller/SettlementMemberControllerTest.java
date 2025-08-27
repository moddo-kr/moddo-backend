package com.dnd.moddo.domain.appointmentMember.controller;

import static com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.dnd.moddo.domain.appointmentMember.dto.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.domain.appointmentMember.dto.request.appointmentMemberSaveRequest;
import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMemberResponse;
import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMembersResponse;
import com.dnd.moddo.global.util.RestDocsTestSupport;

public class SettlementMemberControllerTest extends RestDocsTestSupport {

	@Test
	@DisplayName("모임원을 성공적으로 조회한다.")
	void getGroupMembers() throws Exception {
		// given
		String groupToken = "groupToken";
		Long groupId = 1L;

		AppointmentMembersResponse mockResponse = AppointmentMembersResponse.of(Collections.emptyList());

		when(querySettlementService.findIdByCode(groupToken)).thenReturn(groupId);
		when(queryAppointmentMemberService.findAll(groupId)).thenReturn(mockResponse);

		// when & then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/group-members")
				.param("groupToken", groupToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.members").isArray());

		verify(querySettlementService).findIdByCode(groupToken);
		verify(queryAppointmentMemberService).findAll(groupId);
	}

	@Test
	@DisplayName("모임원을 성공적으로 추가한다.")
	void saveGroupMember() throws Exception {
		// given
		String groupToken = "groupToken";
		Long groupId = 1L;

		appointmentMemberSaveRequest request = new appointmentMemberSaveRequest("김반숙");
		AppointmentMemberResponse response = new AppointmentMemberResponse(1L, PARTICIPANT, "김반숙",
			"https://moddo-s3.s3.amazonaws.com/profile/1.png", false, null);

		when(querySettlementService.findIdByCode(groupToken)).thenReturn(groupId);
		when(commandAppointmentMemberService.addAppointmentMember(groupId, request)).thenReturn(response);

		// when & then
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/group-members")
				.param("groupToken", groupToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(response.id()))
			.andExpect(jsonPath("$.name").value("김반숙"));
	}

	@Test
	@DisplayName("결제 상태를 성공적으로 변경한다.")
	void updatePaymentStatus() throws Exception {
		// given
		String groupToken = "groupToken";
		Long groupMemberId = 1L;

		PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest(true);
		AppointmentMemberResponse response = new AppointmentMemberResponse(1L, PARTICIPANT, "김반숙",
			"https://moddo-s3.s3.amazonaws.com/profile/1.png", true, LocalDateTime.now());

		when(commandAppointmentMemberService.updatePaymentStatus(groupMemberId, request)).thenReturn(response);

		// when & then
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/group-members/{groupMemberId}/payment", groupMemberId)
				.param("groupToken", groupToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isPaid").value(true));
	}

	@Test
	@DisplayName("모임원을 성공적으로 삭제한다.")
	void deleteGroupMember() throws Exception {
		// given
		Long groupMemberId = 1L;

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/group-members/{groupMemberId}", groupMemberId))
			.andExpect(status().isNoContent());

		// when & then
		verify(commandAppointmentMemberService).delete(groupMemberId);
	}

}
