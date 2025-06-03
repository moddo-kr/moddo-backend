package com.dnd.moddo.domain.expense.controller;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.dnd.moddo.domain.expense.dto.request.ExpenseImageRequest;
import com.dnd.moddo.domain.expense.dto.request.ExpenseRequest;
import com.dnd.moddo.domain.expense.dto.request.ExpensesRequest;
import com.dnd.moddo.domain.expense.dto.response.ExpenseDetailsResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpensesResponse;
import com.dnd.moddo.domain.expense.exception.ExpenseNotFoundException;
import com.dnd.moddo.domain.groupMember.exception.GroupMemberNotFoundException;
import com.dnd.moddo.global.util.RestDocsTestSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ExpenseControllerTest extends RestDocsTestSupport {

	private final String groupToken = "groupToken";
	private final Long groupId = 1L;
	private final Long expenseId = 100L;

	private final ObjectMapper objectMapper = new ObjectMapper()
		.registerModule(new JavaTimeModule())
		.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	@Test
	@DisplayName("지출을 정상적으로 저장한다")
	void saveExpensesSuccess() throws Exception {
		ExpensesRequest request = new ExpensesRequest(
			List.of(new ExpenseRequest(100000L, "지출", LocalDate.now(), List.of()))
		);
		ExpensesResponse response = new ExpensesResponse(Collections.emptyList());

		when(jwtService.getGroupId(groupToken)).thenReturn(groupId);
		when(commandExpenseService.createExpenses(eq(groupId), any())).thenReturn(response);

		mockMvc.perform(post("/api/v1/expenses")
				.param("groupToken", groupToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("전체 지출 목록을 정상적으로 조회한다")
	void getAllByGroupIdSuccess() throws Exception {
		when(jwtService.getGroupId(groupToken)).thenReturn(groupId);
		when(queryExpenseService.findAllByGroupId(groupId)).thenReturn(new ExpensesResponse(List.of()));

		mockMvc.perform(get("/api/v1/expenses")
				.param("groupToken", groupToken))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("단일 지출 내역을 정상적으로 조회한다")
	void getByExpenseIdSuccess() throws Exception {
		when(queryExpenseService.findOneByExpenseId(expenseId)).thenReturn(mock(ExpenseResponse.class));

		mockMvc.perform(get("/api/v1/expenses/{expenseId}", expenseId))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("지출 상세 내역을 정상적으로 조회한다")
	void getExpenseDetailsSuccess() throws Exception {
		when(jwtService.getGroupId(groupToken)).thenReturn(groupId);
		when(queryExpenseService.findAllExpenseDetailsByGroupId(groupId)).thenReturn(
			mock(ExpenseDetailsResponse.class));

		mockMvc.perform(get("/api/v1/expenses/details")
				.param("groupToken", groupToken))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("지출을 정상적으로 수정한다")
	void updateExpenseSuccess() throws Exception {
		ExpenseRequest request = new ExpenseRequest(100000L, "지출", LocalDate.now(), List.of());

		when(commandExpenseService.update(eq(expenseId), any())).thenReturn(mock(ExpenseResponse.class));

		mockMvc.perform(put("/api/v1/expenses/{expenseId}", expenseId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("지출을 정상적으로 삭제한다")
	void deleteExpenseSuccess() throws Exception {
		doNothing().when(commandExpenseService).delete(expenseId);

		mockMvc.perform(delete("/api/v1/expenses/{expenseId}", expenseId))
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("지출 이미지 URL을 정상적으로 업데이트한다")
	void updateImgUrlSuccess() throws Exception {
		ExpenseImageRequest request = new ExpenseImageRequest(List.of(
			"https://image1.com",
			"https://image2.com"
		));

		when(jwtService.getGroupId(groupToken)).thenReturn(groupId);
		when(jwtService.getUserId(any())).thenReturn(10L);
		doNothing().when(commandExpenseService).updateImgUrl(anyLong(), anyLong(), eq(expenseId), any());

		mockMvc.perform(put("/api/v1/expenses/img/{expenseId}", expenseId)
				.param("groupToken", groupToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("참여자를 찾을 수 없으면 예외를 반환한다 - saveExpenses")
	void saveExpensesFail_whenMemberNotFound() throws Exception {
		ExpensesRequest request = new ExpensesRequest(
			List.of(new ExpenseRequest(100000L, "지출", LocalDate.now(), List.of())));

		when(jwtService.getGroupId(groupToken)).thenReturn(groupId);
		when(commandExpenseService.createExpenses(eq(groupId), any()))
			.thenThrow(new GroupMemberNotFoundException(1L));

		mockMvc.perform(post("/api/v1/expenses")
				.param("groupToken", groupToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("지출 내역을 찾을 수 없으면 예외를 반환한다 - getByExpenseId")
	void getByExpenseIdFail_whenExpenseNotFound() throws Exception {
		when(queryExpenseService.findOneByExpenseId(expenseId))
			.thenThrow(new ExpenseNotFoundException(expenseId));

		mockMvc.perform(get("/api/v1/expenses/{expenseId}", expenseId))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("지출 내역을 찾을 수 없으면 예외를 반환한다 - getAllByGroupId")
	void getAllByGroupIdFail_whenExpenseNotFound() throws Exception {
		when(jwtService.getGroupId(groupToken)).thenReturn(groupId);
		when(queryExpenseService.findAllByGroupId(groupId))
			.thenThrow(new ExpenseNotFoundException(expenseId));

		mockMvc.perform(get("/api/v1/expenses")
				.param("groupToken", groupToken))
			.andExpect(status().isNotFound());
	}
}
