package com.dnd.moddo.domain.paymentRequest.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.PaymentRequestReader;
import com.dnd.moddo.event.application.query.QueryPaymentRequestService;
import com.dnd.moddo.event.presentation.response.PaymentRequestItemResponse;
import com.dnd.moddo.event.presentation.response.PaymentRequestsResponse;

@ExtendWith(MockitoExtension.class)
class QueryPaymentRequestServiceTest {

	@Mock
	private PaymentRequestReader paymentRequestReader;

	@InjectMocks
	private QueryPaymentRequestService queryPaymentRequestService;

	@Test
	@DisplayName("대상 유저 기준으로 입금 확인 요청 목록을 조회할 수 있다.")
	void findByTargetUserId() {
		PaymentRequestsResponse expected = new PaymentRequestsResponse(
			List.of(new PaymentRequestItemResponse(
				LocalDateTime.now(),
				1L,
				2L,
				"김반숙",
				"https://moddo-s3.s3.amazonaws.com/profile/1.png",
				10000L
			))
		);
		when(paymentRequestReader.findByTargetUserId(1L)).thenReturn(expected);

		PaymentRequestsResponse result = queryPaymentRequestService.findByTargetUserId(1L);

		assertThat(result).isEqualTo(expected);
		verify(paymentRequestReader).findByTargetUserId(1L);
	}
}
