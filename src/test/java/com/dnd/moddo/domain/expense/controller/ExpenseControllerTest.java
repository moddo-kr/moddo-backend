package com.dnd.moddo.domain.expense.controller;

import static com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.dnd.moddo.domain.expense.dto.request.ExpenseImageRequest;
import com.dnd.moddo.domain.expense.dto.request.ExpenseRequest;
import com.dnd.moddo.domain.expense.dto.request.ExpensesRequest;
import com.dnd.moddo.domain.expense.dto.response.ExpenseDetailResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpenseDetailsResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpensesResponse;
import com.dnd.moddo.domain.expense.exception.ExpenseNotFoundException;
import com.dnd.moddo.domain.groupMember.exception.GroupMemberNotFoundException;
import com.dnd.moddo.domain.memberExpense.dto.request.MemberExpenseRequest;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseResponse;
import com.dnd.moddo.global.util.RestDocsTestSupport;

public class ExpenseControllerTest extends RestDocsTestSupport {

	private final String groupToken = "groupToken";
	private final Long groupId = 1L;
	private final Long expenseId = 1L;

	@Test
	@DisplayName("지출 내역을 정상적으로 저장한다.")
	void saveExpensesSuccess() throws Exception {
		ExpensesRequest request = new ExpensesRequest(List.of(
			new ExpenseRequest(
				20000L, "카페", LocalDate.of(2025, 2, 3),
				List.of(
					new MemberExpenseRequest(1L, 9000L),
					new MemberExpenseRequest(2L, 11000L)
				)
			)
		));

		ExpensesResponse response = new ExpensesResponse(List.of(
			new ExpenseResponse(
				1L, 100000L, "하이디라오", LocalDate.of(2025, 2, 3),
				List.of(
					new MemberExpenseResponse(1L, MANAGER, "김모또",
						"https://moddo-s3.s3.amazonaws.com/profile/MODDO.png", 50000L),
					new MemberExpenseResponse(2L, PARTICIPANT, "군계란",
						"https://moddo-s3.s3.amazonaws.com/profile/1.png", 50000L)
				)
			),
			new ExpenseResponse(
				2L, 20000L, "카페", LocalDate.of(2025, 2, 3),
				List.of(
					new MemberExpenseResponse(1L, MANAGER, "김모또",
						"https://moddo-s3.s3.amazonaws.com/profile/MODDO.png", 9000L),
					new MemberExpenseResponse(2L, PARTICIPANT, "군계란",
						"https://moddo-s3.s3.amazonaws.com/profile/1.png", 11000L)
				)
			)
		));

		when(queryGroupService.findIdByCode(groupToken)).thenReturn(groupId);
		when(commandExpenseService.createExpenses(eq(groupId), any())).thenReturn(response);

		mockMvc.perform(post("/api/v1/expenses")
				.param("groupToken", groupToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andDo(document("create-expense",
				requestFields(
					fieldWithPath("expenses").type(JsonFieldType.ARRAY).description("지출 항목 목록"),
					fieldWithPath("expenses[].amount").type(JsonFieldType.NUMBER).description("지출 금액"),
					fieldWithPath("expenses[].content").type(JsonFieldType.STRING).description("지출 내용"),
					fieldWithPath("expenses[].date").type(JsonFieldType.STRING).description("지출 일자 (yyyy-MM-dd)"),
					fieldWithPath("expenses[].memberExpenses").type(JsonFieldType.ARRAY).description("멤버별 지출 내역"),
					fieldWithPath("expenses[].memberExpenses[].id").type(JsonFieldType.NUMBER)
						.description("멤버 ID"),
					fieldWithPath("expenses[].memberExpenses[].amount").type(JsonFieldType.NUMBER)
						.description("멤버가 실제로 부담한 금액")
				))
			)
		;
	}

	@Test
	@DisplayName("전체 지출 목록을 정상적으로 조회한다.")
	void getAllByGroupIdSuccess() throws Exception {
		// given
		List<ExpenseResponse> expenseResponses = List.of(
			new ExpenseResponse(1L, 100000L, "지출", LocalDate.of(2025, 2, 3), List.of(
				new MemberExpenseResponse(1L, MANAGER, "김모또", "https://moddo-s3.s3.amazonaws.com/profile/MODDO.png",
					1000L),
				new MemberExpenseResponse(2L, PARTICIPANT, "군계란", "https://moddo-s3.s3.amazonaws.com/profile/1.png",
					50000L)
			)),
			new ExpenseResponse(2L, 22000L, "카페", LocalDate.of(2025, 2, 3), List.of(
				new MemberExpenseResponse(1L, MANAGER, "김모또", "https://moddo-s3.s3.amazonaws.com/profile/MODDO.png",
					10000L),
				new MemberExpenseResponse(2L, PARTICIPANT, "군계란", "https://moddo-s3.s3.amazonaws.com/profile/1.png",
					12000L)
			)),
			new ExpenseResponse(3L, 210000L, "향수공방", LocalDate.of(2025, 2, 3), List.of(
				new MemberExpenseResponse(1L, MANAGER, "김모또", "https://moddo-s3.s3.amazonaws.com/profile/MODDO.png",
					70000L),
				new MemberExpenseResponse(2L, PARTICIPANT, "군계란", "https://moddo-s3.s3.amazonaws.com/profile/1.png",
					70000L),
				new MemberExpenseResponse(3L, PARTICIPANT, "연노른자", "https://moddo-s3.s3.amazonaws.com/profile/2.png",
					70000L)
			)),
			new ExpenseResponse(4L, 36000L, "간술", LocalDate.of(2025, 2, 3), List.of(
				new MemberExpenseResponse(1L, MANAGER, "김모또", "https://moddo-s3.s3.amazonaws.com/profile/MODDO.png",
					12000L),
				new MemberExpenseResponse(2L, PARTICIPANT, "군계란", "https://moddo-s3.s3.amazonaws.com/profile/1.png",
					12000L),
				new MemberExpenseResponse(3L, PARTICIPANT, "연노른자", "https://moddo-s3.s3.amazonaws.com/profile/2.png",
					12000L)
			))
		);

		ExpensesResponse response = new ExpensesResponse(expenseResponses);

		when(queryGroupService.findIdByCode(groupToken)).thenReturn(groupId);
		when(queryExpenseService.findAllByGroupId(groupId)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/v1/expenses")
				.param("groupToken", groupToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expenses.length()").value(4))
			.andExpect(jsonPath("$.expenses[0].id").value(1))
			.andExpect(jsonPath("$.expenses[0].memberExpenses[0].name").value("김모또"))
			.andDo(document("get-expenses",
				queryParameters(
					parameterWithName("groupToken").description("조회할 그룹 토큰")
				),
				responseFields(
					fieldWithPath("expenses").type(JsonFieldType.ARRAY).description("지출 내역 목록"),
					fieldWithPath("expenses[].id").type(JsonFieldType.NUMBER).description("지출 ID"),
					fieldWithPath("expenses[].amount").type(JsonFieldType.NUMBER).description("지출 금액"),
					fieldWithPath("expenses[].content").type(JsonFieldType.STRING).description("지출 내역 내용"),
					fieldWithPath("expenses[].date").type(JsonFieldType.STRING).description("지출 날짜 (yyyy-MM-dd)"),
					fieldWithPath("expenses[].memberExpenses").type(JsonFieldType.ARRAY).description("멤버별 지출 상세"),
					fieldWithPath("expenses[].memberExpenses[].id").type(JsonFieldType.NUMBER).description("멤버 ID"),
					fieldWithPath("expenses[].memberExpenses[].name").type(JsonFieldType.STRING).description("멤버 이름"),
					fieldWithPath("expenses[].memberExpenses[].role").type(JsonFieldType.STRING)
						.description("멤버 역할(MANAGER/PARTICIPANT)"),
					fieldWithPath("expenses[].memberExpenses[].profile").type(JsonFieldType.STRING)
						.description("프로필 이미지 URL"),
					fieldWithPath("expenses[].memberExpenses[].amount").type(JsonFieldType.NUMBER)
						.description("해당 멤버의 지출 금액")
				)
			));
	}

	@Test
	@DisplayName("단일 지출 내역을 정상적으로 조회한다.")
	void getByExpenseIdSuccess() throws Exception {
		// given
		ExpenseResponse response = new ExpenseResponse(
			1L, 25000L, "카페", LocalDate.of(2025, 2, 3), List.of(
			new MemberExpenseResponse(1L, MANAGER, "김모또", "https://moddo-s3.s3.amazonaws.com/profile/MODDO.png",
				12000L))
		);

		when(queryExpenseService.findOneByExpenseId(expenseId)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/v1/expenses/{expenseId}", expenseId)
				.param("groupToken", groupToken))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("지출 상세 내역을 정상적으로 조회한다.")
	void getExpenseDetailsSuccess() throws Exception {
		// given
		List<ExpenseDetailResponse> expenseDetail = List.of(
			new ExpenseDetailResponse(1L, LocalDate.of(2025, 2, 3), "하이디라오", 100000L, List.of("김모또(총무)", "김반숙")),
			new ExpenseDetailResponse(2L, LocalDate.of(2025, 2, 3), "카페", 22000L, List.of("김모또(총무)", "김반숙")),
			new ExpenseDetailResponse(3L, LocalDate.of(2025, 2, 3), "향수공방", 210000L, List.of("김모또(총무)", "김반숙", "정에그")),
			new ExpenseDetailResponse(4L, LocalDate.of(2025, 2, 3), "간술", 36000L, List.of("김모또(총무)", "김반숙", "정에그"))
		);
		ExpenseDetailsResponse response = new ExpenseDetailsResponse(expenseDetail);

		given(queryGroupService.findIdByCode(groupToken)).willReturn(groupId);
		given(queryExpenseService.findAllExpenseDetailsByGroupId(groupId)).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/v1/expenses/details")
				.param("groupToken", groupToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expenses", hasSize(4)))
			.andExpect(jsonPath("$.expenses[0].id").value(1))
			.andExpect(jsonPath("$.expenses[0].content").value("하이디라오"))
			.andExpect(jsonPath("$.expenses[0].totalAmount").value(100000))
			.andExpect(jsonPath("$.expenses[0].groupMembers[0]").value("김모또(총무)"));
	}

	@Test
	@DisplayName("지출 내역을 정상적으로 수정한다.")
	void updateExpenseSuccess() throws Exception {
		// given
		ExpenseRequest request = new ExpenseRequest(
			28000L,
			"카페",
			LocalDate.of(2025, 2, 2),
			List.of(
				new MemberExpenseRequest(1L, 14000L),
				new MemberExpenseRequest(2L, 14000L)
			)
		);

		ExpenseResponse response = new ExpenseResponse(
			1L,
			28000L,
			"카페",
			LocalDate.of(2025, 2, 2),
			List.of(
				new MemberExpenseResponse(1L, MANAGER, "김모또", "https://moddo-s3.s3.amazonaws.com/profile/MODDO.png",
					14000L),
				new MemberExpenseResponse(2L, PARTICIPANT, "김반숙", "https://moddo-s3.s3.amazonaws.com/profile/1.png",
					14000L)
			)
		);

		given(commandExpenseService.update(eq(expenseId), any())).willReturn(response);

		// when & then
		mockMvc.perform(put("/api/v1/expenses/{expenseId}", expenseId)
				.param("groupToken", groupToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("지출 내역을 정상적으로 삭제한다.")
	void deleteExpenseSuccess() throws Exception {
		doNothing().when(commandExpenseService).delete(expenseId);

		mockMvc.perform(delete("/api/v1/expenses/{expenseId}", expenseId)
				.param("groupToken", groupToken))
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("지출 이미지 URL을 정상적으로 업데이트한다.")
	void updateImgUrlSuccess() throws Exception {
		ExpenseImageRequest request = new ExpenseImageRequest(List.of(
			"https://image1.com",
			"https://image2.com"
		));

		mockMvc.perform(put("/api/v1/expenses/img/{expenseId}", expenseId)
				.param("groupToken", groupToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("참여자를 찾을 수 없으면 예외를 반환한다.")
	void saveExpensesFail_whenMemberNotFound() throws Exception {
		ExpensesRequest request = new ExpensesRequest(
			List.of(new ExpenseRequest(100000L, "지출", LocalDate.now(), List.of())));

		when(queryGroupService.findIdByCode(groupToken)).thenReturn(groupId);
		when(commandExpenseService.createExpenses(eq(groupId), any()))
			.thenThrow(new GroupMemberNotFoundException(1L));

		mockMvc.perform(post("/api/v1/expenses")
				.param("groupToken", groupToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("지출 내역을 찾을 수 없으면 예외를 반환한다.")
	void getByExpenseIdFail_whenExpenseNotFound() throws Exception {
		when(queryExpenseService.findOneByExpenseId(expenseId))
			.thenThrow(new ExpenseNotFoundException(expenseId));

		mockMvc.perform(get("/api/v1/expenses/{expenseId}", expenseId))
			.andExpect(status().isNotFound());
	}
}