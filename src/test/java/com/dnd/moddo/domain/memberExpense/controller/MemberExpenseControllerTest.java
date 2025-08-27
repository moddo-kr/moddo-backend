package com.dnd.moddo.domain.memberExpense.controller;

import static com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMemberExpenseResponse;
import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMembersExpenseResponse;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseDetailResponse;
import com.dnd.moddo.global.util.RestDocsTestSupport;

public class MemberExpenseControllerTest extends RestDocsTestSupport {

	@Test
	@DisplayName("모임원별 상세 지출 내역을 성공적으로 조회한다.")
	void getMemberExpensesDetailsSuccess() throws Exception {
		// given
		String groupToken = "mockedGroupToken";
		Long groupId = 1L;

		AppointmentMembersExpenseResponse appointmentMembersExpenseResponse = new AppointmentMembersExpenseResponse(
			List.of(
				new AppointmentMemberExpenseResponse(1L, MANAGER, "김모또", 10000L,
					"https://moddo-s3.s3.amazonaws.com/profile/MODDO.png", true, LocalDateTime.now(),
					List.of(new MemberExpenseDetailResponse("카페", 10000L))
				),
				new AppointmentMemberExpenseResponse(2L, PARTICIPANT, "군계란", 10000L,
					"https://moddo-s3.s3.amazonaws.com/profile/1.png", false, null,
					List.of(new MemberExpenseDetailResponse("카페", 10000L))
				)
			)
		);

		when(querySettlementService.findIdByCode(groupToken)).thenReturn(groupId);
		when(queryMemberExpenseService.findMemberExpenseDetailsByGroupId(groupId)).thenReturn(
			appointmentMembersExpenseResponse);

		// when & then
		mockMvc.perform(get("/api/v1/member-expenses")
				.param("groupToken", groupToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memberExpenses").isArray());

		verify(querySettlementService, times(1)).findIdByCode(groupToken);
		verify(queryMemberExpenseService, times(1)).findMemberExpenseDetailsByGroupId(groupId);
	}
}
