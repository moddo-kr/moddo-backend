package com.dnd.moddo.domain.memberExpense.controller;

import static com.dnd.moddo.event.domain.member.ExpenseRole.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus;
import com.dnd.moddo.event.presentation.response.MemberExpenseDetailResponse;
import com.dnd.moddo.event.presentation.response.MemberExpenseItemResponse;
import com.dnd.moddo.event.presentation.response.MembersExpenseResponse;
import com.dnd.moddo.global.util.RestDocsTestSupport;

public class MemberExpenseControllerTest extends RestDocsTestSupport {

	@Test
	@DisplayName("모임원별 상세 지출 내역을 성공적으로 조회한다.")
	void getMemberExpensesDetailsSuccess() throws Exception {
		// given
		String code = "mockedCode";
		Long groupId = 1L;

		MembersExpenseResponse membersExpenseResponse = new MembersExpenseResponse(
			List.of(
				new MemberExpenseItemResponse(1L, MANAGER, "김모또", 10000L,
					"https://moddo-s3.s3.amazonaws.com/profile/MODDO.png", true, LocalDateTime.now(),
					null, null, null, List.of(new MemberExpenseDetailResponse("카페", 10000L))
				),
				new MemberExpenseItemResponse(2L, PARTICIPANT, "군계란", 10000L,
					"https://moddo-s3.s3.amazonaws.com/profile/1.png", false, null,
					100L, PaymentRequestStatus.APPROVED, "승인완료",
					List.of(new MemberExpenseDetailResponse("카페", 10000L))
				)
			)
		);

		when(loginUserArgumentResolver.supportsParameter(any()))
			.thenReturn(true);
		when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.thenReturn(new LoginUserInfo(1L, "USER"));
		when(querySettlementService.findIdByCode(code)).thenReturn(groupId);
		when(queryMemberExpenseService.findMemberExpenseDetailsBySettlementId(groupId, 1L)).thenReturn(
			membersExpenseResponse);

		// when & then
		mockMvc.perform(get("/api/v1/groups/{code}/member-expenses", code)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memberExpenses").isArray())
			.andExpect(jsonPath("$.memberExpenses[1].paymentRequestId").value(100L))
			.andExpect(jsonPath("$.memberExpenses[1].paymentRequestStatus").value("APPROVED"))
			.andExpect(jsonPath("$.memberExpenses[1].paymentRequestStatusLabel").value("승인완료"));

		verify(querySettlementService, times(1)).findIdByCode(code);
		verify(queryMemberExpenseService, times(1)).findMemberExpenseDetailsBySettlementId(groupId, 1L);
	}
}
