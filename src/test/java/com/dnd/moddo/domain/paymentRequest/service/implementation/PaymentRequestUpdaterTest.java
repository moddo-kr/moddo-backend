package com.dnd.moddo.domain.paymentRequest.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.MemberUpdater;
import com.dnd.moddo.event.application.impl.PaymentRequestUpdater;
import com.dnd.moddo.event.application.impl.PaymentRequestValidator;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequest;
import com.dnd.moddo.event.infrastructure.PaymentRequestRepository;

@ExtendWith(MockitoExtension.class)
class PaymentRequestUpdaterTest {

	@Mock
	private PaymentRequestRepository paymentRequestRepository;

	@Mock
	private PaymentRequestValidator paymentRequestValidator;

	@Mock
	private MemberUpdater memberUpdater;

	@InjectMocks
	private PaymentRequestUpdater paymentRequestUpdater;

	@Test
	@DisplayName("입금 확인 요청을 승인할 수 있다.")
	void approvePaymentRequestSuccess() {
		Long paymentRequestId = 1L;
		Long userId = 100L;
			PaymentRequest paymentRequest = mock(PaymentRequest.class);

			when(paymentRequestRepository.getById(paymentRequestId)).thenReturn(paymentRequest);
			when(paymentRequest.getRequestMemberId()).thenReturn(10L);

			PaymentRequest result = paymentRequestUpdater.approvePaymentRequest(paymentRequestId, userId);

			assertThat(result).isEqualTo(paymentRequest);
			verify(paymentRequestValidator).validateProcessRequest(paymentRequest, userId);
			verify(memberUpdater).updatePaymentStatus(10L, true);
			verify(paymentRequest).approve();
		}

	@Test
	@DisplayName("입금 확인 요청을 거절할 수 있다.")
	void rejectPaymentRequestSuccess() {
		Long paymentRequestId = 1L;
		Long userId = 100L;
		PaymentRequest paymentRequest = mock(PaymentRequest.class);

		when(paymentRequestRepository.getById(paymentRequestId)).thenReturn(paymentRequest);

		PaymentRequest result = paymentRequestUpdater.rejectPaymentRequest(paymentRequestId, userId);

		assertThat(result).isEqualTo(paymentRequest);
		verify(paymentRequestValidator).validateProcessRequest(paymentRequest, userId);
		verify(paymentRequest).reject();
		verify(memberUpdater, never()).updatePaymentStatus(anyLong(), anyBoolean());
	}
}
