package com.dnd.moddo.domain.paymentRequest.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import com.dnd.moddo.auth.infrastructure.security.LoginUserArgumentResolver;
import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus;
import com.dnd.moddo.event.presentation.response.PaymentRequestResponse;
import com.dnd.moddo.event.presentation.response.PaymentRequestsResponse;
import com.dnd.moddo.global.util.RestDocsTestSupport;

public class PaymentRequestControllerTest extends RestDocsTestSupport {

	@BeforeEach
	void setUpLoginUser() throws Exception {
		when(loginUserArgumentResolver.supportsParameter(any())).thenReturn(true);
		when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.thenReturn(new LoginUserInfo(1L, "USER"));
	}

	@Test
	@DisplayName("내게 온 입금 확인 요청 목록을 조회한다.")
	void getPaymentRequests() throws Exception {
		PaymentRequestsResponse response = new PaymentRequestsResponse(
			java.util.List.of(
				new com.dnd.moddo.event.presentation.response.PaymentRequestItemResponse(
					LocalDateTime.of(2026, 3, 13, 22, 0),
					1L,
					2L,
					"김반숙",
					"profile-1.png",
					12000L
				)
			)
		);

		when(queryPaymentRequestService.findByTargetUserId(1L)).thenReturn(response);

		mockMvc.perform(get("/api/v1/payments"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.paymentRequests").isArray())
			.andExpect(jsonPath("$.paymentRequests[0].paymentRequestId").value(1L))
			.andExpect(jsonPath("$.paymentRequests[0].name").value("김반숙"))
			.andExpect(jsonPath("$.paymentRequests[0].profileUrl").value("profile-1.png"))
			.andExpect(jsonPath("$.paymentRequests[0].totalAmount").value(12000L))
			.andDo(restDocs.document(
				responseFields(
					fieldWithPath("paymentRequests").type(JsonFieldType.ARRAY).description("입금 확인 요청 목록"),
					fieldWithPath("paymentRequests[].requestedAt").type(JsonFieldType.STRING).description("요청 시각"),
					fieldWithPath("paymentRequests[].paymentRequestId").type(JsonFieldType.NUMBER)
						.description("입금 확인 요청 ID"),
					fieldWithPath("paymentRequests[].memberId").type(JsonFieldType.NUMBER).description("요청 참여자 ID"),
					fieldWithPath("paymentRequests[].name").type(JsonFieldType.STRING).description("요청 참여자 이름"),
					fieldWithPath("paymentRequests[].profileUrl").type(JsonFieldType.STRING).description("요청 참여자 프로필 URL"),
					fieldWithPath("paymentRequests[].totalAmount").type(JsonFieldType.NUMBER).description("요청 금액")
				)
			));
	}

	@Test
	@DisplayName("입금 확인 요청을 생성한다.")
	void createPaymentRequest() throws Exception {
		String code = "code";
		Long settlementId = 1L;
		PaymentRequestResponse response = new PaymentRequestResponse(
			1L, settlementId, 2L, 3L, LocalDateTime.of(2026, 3, 13, 22, 0), null, PaymentRequestStatus.PENDING
		);

		when(querySettlementService.findIdByCode(code)).thenReturn(settlementId);
		when(commandPaymentRequest.createPaymentRequest(settlementId, 1L)).thenReturn(response);

		mockMvc.perform(post("/api/v1/groups/{code}/payments", code)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.settlementId").value(settlementId))
			.andDo(restDocs.document(
				pathParameters(
					parameterWithName("code").description("정산 코드")
				),
				responseFields(
					fieldWithPath("id").type(JsonFieldType.NUMBER).description("입금 확인 요청 ID"),
					fieldWithPath("settlementId").type(JsonFieldType.NUMBER).description("정산 ID"),
					fieldWithPath("requestMemberId").type(JsonFieldType.NUMBER).description("요청 참여자 ID"),
					fieldWithPath("targetUserId").type(JsonFieldType.NUMBER).description("처리 대상 사용자 ID"),
					fieldWithPath("requestedAt").type(JsonFieldType.STRING).description("요청 시각"),
					fieldWithPath("processedAt").type(JsonFieldType.NULL).description("처리 시각").optional(),
					fieldWithPath("status").type(JsonFieldType.STRING).description("요청 상태")
				)
			));
	}

	@Test
	@DisplayName("입금 확인 요청을 승인한다.")
	void approvePaymentRequest() throws Exception {
		PaymentRequestResponse response = new PaymentRequestResponse(
			1L, 1L, 2L, 1L,
			LocalDateTime.of(2026, 3, 13, 22, 0),
			LocalDateTime.of(2026, 3, 13, 22, 5),
			com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus.APPROVED
		);

		when(commandPaymentRequest.approvePaymentRequest(1L, 1L)).thenReturn(response);

		mockMvc.perform(patch("/api/v1/payments/{paymentRequestId}/approve", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("APPROVED"))
			.andDo(restDocs.document(
				pathParameters(
					parameterWithName("paymentRequestId").description("입금 확인 요청 ID")
				),
				responseFields(
					fieldWithPath("id").type(JsonFieldType.NUMBER).description("입금 확인 요청 ID"),
					fieldWithPath("settlementId").type(JsonFieldType.NUMBER).description("정산 ID"),
					fieldWithPath("requestMemberId").type(JsonFieldType.NUMBER).description("요청 참여자 ID"),
					fieldWithPath("targetUserId").type(JsonFieldType.NUMBER).description("처리 대상 사용자 ID"),
					fieldWithPath("requestedAt").type(JsonFieldType.STRING).description("요청 시각"),
					fieldWithPath("processedAt").type(JsonFieldType.STRING).description("처리 시각"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("요청 상태")
				)
			));
	}

	@Test
	@DisplayName("입금 확인 요청을 거절한다.")
	void rejectPaymentRequest() throws Exception {
		PaymentRequestResponse response = new PaymentRequestResponse(
			1L, 1L, 2L, 1L,
			LocalDateTime.of(2026, 3, 13, 22, 0),
			LocalDateTime.of(2026, 3, 13, 22, 5),
			com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus.REJECTED
		);

		when(commandPaymentRequest.rejectPaymentRequest(1L, 1L)).thenReturn(response);

		mockMvc.perform(patch("/api/v1/payments/{paymentRequestId}/reject", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("REJECTED"))
			.andDo(restDocs.document(
				pathParameters(
					parameterWithName("paymentRequestId").description("입금 확인 요청 ID")
				),
				responseFields(
					fieldWithPath("id").type(JsonFieldType.NUMBER).description("입금 확인 요청 ID"),
					fieldWithPath("settlementId").type(JsonFieldType.NUMBER).description("정산 ID"),
					fieldWithPath("requestMemberId").type(JsonFieldType.NUMBER).description("요청 참여자 ID"),
					fieldWithPath("targetUserId").type(JsonFieldType.NUMBER).description("처리 대상 사용자 ID"),
					fieldWithPath("requestedAt").type(JsonFieldType.STRING).description("요청 시각"),
					fieldWithPath("processedAt").type(JsonFieldType.STRING).description("처리 시각"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("요청 상태")
				)
			));
	}
}
