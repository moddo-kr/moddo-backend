package com.dnd.moddo.domain.groupMember.controller;

import static com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.dnd.moddo.domain.groupMember.dto.request.GroupMemberSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMemberResponse;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersResponse;
import com.dnd.moddo.global.util.RestDocsTestSupport;

public class GroupMemberControllerTest extends RestDocsTestSupport {

	@Test
	@DisplayName("모임원을 성공적으로 조회한다.")
	void getGroupMembers() throws Exception {
		String groupToken = "groupToken";
		Long groupId = 1L;

		GroupMembersResponse mockResponse = GroupMembersResponse.of(Collections.emptyList());

		when(jwtService.getGroupId(groupToken)).thenReturn(groupId);
		when(queryGroupMemberService.findAll(groupId)).thenReturn(mockResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/group-members")
				.param("groupToken", groupToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.members").isArray());

		verify(jwtService).getGroupId(groupToken);
		verify(queryGroupMemberService).findAll(groupId);
	}

	@Test
	@DisplayName("모임원을 성공적으로 추가한다.")
	void saveGroupMember() throws Exception {
		// given
		String groupToken = "groupToken";
		Long groupId = 1L;

		GroupMemberSaveRequest request = new GroupMemberSaveRequest("김반숙");
		GroupMemberResponse response = new GroupMemberResponse(1L, PARTICIPANT, "김반숙",
			"https://moddo-s3.s3.amazonaws.com/profile/1.png", false, null);

		when(jwtService.getGroupId(groupToken)).thenReturn(groupId);
		when(commandGroupMemberService.addGroupMember(groupId, request)).thenReturn(response);

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
		GroupMemberResponse response = new GroupMemberResponse(1L, PARTICIPANT, "김반숙",
			"https://moddo-s3.s3.amazonaws.com/profile/1.png", true, LocalDateTime.now());

		when(commandGroupMemberService.updatePaymentStatus(groupMemberId, request)).thenReturn(response);

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
		verify(commandGroupMemberService).delete(groupMemberId);
	}

}
