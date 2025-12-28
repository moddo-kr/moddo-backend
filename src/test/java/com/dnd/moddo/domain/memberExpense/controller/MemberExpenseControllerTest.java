package com.dnd.moddo.domain.memberExpense.controller;

import static com.dnd.moddo.event.domain.member.ExpenseRole.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import com.dnd.moddo.event.presentation.response.MemberExpenseDetailResponse;
import com.dnd.moddo.event.presentation.response.MemberExpenseItemResponse;
import com.dnd.moddo.event.presentation.response.MembersExpenseResponse;
import com.dnd.moddo.global.logging.ErrorNotifier;
import com.dnd.moddo.global.util.RestDocsTestSupport;

public class MemberExpenseControllerTest extends RestDocsTestSupport {

	@MockBean
	ErrorNotifier errorNotifier;

	@Test
	@DisplayName("모임원별 상세 지출 내역을 성공적으로 조회한다.")
	void getMemberExpensesDetailsSuccess() throws Exception {
		// given
		String groupToken = "mockedGroupToken";
		Long groupId = 1L;

		MembersExpenseResponse membersExpenseResponse = new MembersExpenseResponse(
			List.of(
				new MemberExpenseItemResponse(1L, MANAGER, "김모또", 10000L,
					"https://moddo-s3.s3.amazonaws.com/profile/MODDO.png", true, LocalDateTime.now(),
					List.of(new MemberExpenseDetailResponse("카페", 10000L))
				),
				new MemberExpenseItemResponse(2L, PARTICIPANT, "군계란", 10000L,
					"https://moddo-s3.s3.amazonaws.com/profile/1.png", false, null,
					List.of(new MemberExpenseDetailResponse("카페", 10000L))
				)
			)
		);

		when(querySettlementService.findIdByCode(groupToken)).thenReturn(groupId);
		when(queryMemberExpenseService.findMemberExpenseDetailsBySettlementId(groupId)).thenReturn(
			membersExpenseResponse);

		// when & then
		mockMvc.perform(get("/api/v1/member-expenses")
				.param("groupToken", groupToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memberExpenses").isArray());

		verify(querySettlementService, times(1)).findIdByCode(groupToken);
		verify(queryMemberExpenseService, times(1)).findMemberExpenseDetailsBySettlementId(groupId);
	}
}
