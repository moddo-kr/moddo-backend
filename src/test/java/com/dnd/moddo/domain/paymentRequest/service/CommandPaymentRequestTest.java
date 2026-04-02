package com.dnd.moddo.domain.paymentRequest.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.common.cache.CacheEvictor;
import com.dnd.moddo.event.application.command.CommandPaymentRequest;
import com.dnd.moddo.event.application.impl.PaymentRequestCreator;
import com.dnd.moddo.event.application.impl.PaymentRequestUpdater;
import com.dnd.moddo.event.application.impl.SettlementCompletionProcessor;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequest;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus;
import com.dnd.moddo.event.presentation.response.PaymentRequestResponse;

@ExtendWith(MockitoExtension.class)
class CommandPaymentRequestTest {

	@Mock
	private PaymentRequestCreator paymentRequestCreator;

	@Mock
	private PaymentRequestUpdater paymentRequestUpdater;

	@Mock
	private SettlementCompletionProcessor settlementCompletionProcessor;
	@Mock
	private CacheEvictor cacheEvictor;

	@InjectMocks
	private CommandPaymentRequest commandPaymentRequest;

	@Test
	@DisplayName("입금 확인 요청을 생성할 수 있다.")
	void createPaymentRequest() {
		PaymentRequest paymentRequest = mock(PaymentRequest.class);
		stubPaymentRequest(paymentRequest, PaymentRequestStatus.PENDING);
		when(paymentRequestCreator.createPaymentRequest(1L, 2L)).thenReturn(paymentRequest);

		PaymentRequestResponse response = commandPaymentRequest.createPaymentRequest(1L, 2L);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.status()).isEqualTo(PaymentRequestStatus.PENDING);
	}

	@Test
	@DisplayName("입금 확인 요청을 승인할 수 있다.")
	void approvePaymentRequest() {
		PaymentRequest paymentRequest = mock(PaymentRequest.class);
		stubPaymentRequest(paymentRequest, PaymentRequestStatus.APPROVED);
			when(paymentRequestUpdater.approvePaymentRequest(1L, 2L)).thenReturn(paymentRequest);

			PaymentRequestResponse response = commandPaymentRequest.approvePaymentRequest(1L, 2L);

			assertThat(response.id()).isEqualTo(1L);
			assertThat(response.status()).isEqualTo(PaymentRequestStatus.APPROVED);
			verify(settlementCompletionProcessor).completeIfAllPaid(2L);
			verify(cacheEvictor).evictMembers(2L);
			verify(cacheEvictor).evictSettlementListsBySettlement(2L);
		}

	@Test
	@DisplayName("입금 확인 요청을 거절할 수 있다.")
	void rejectPaymentRequest() {
		PaymentRequest paymentRequest = mock(PaymentRequest.class);
		stubPaymentRequest(paymentRequest, PaymentRequestStatus.REJECTED);
		when(paymentRequestUpdater.rejectPaymentRequest(1L, 2L)).thenReturn(paymentRequest);

		PaymentRequestResponse response = commandPaymentRequest.rejectPaymentRequest(1L, 2L);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.status()).isEqualTo(PaymentRequestStatus.REJECTED);
	}

	private void stubPaymentRequest(PaymentRequest paymentRequest, PaymentRequestStatus status) {
		when(paymentRequest.getId()).thenReturn(1L);
		when(paymentRequest.getSettlementId()).thenReturn(2L);
		when(paymentRequest.getRequestMemberId()).thenReturn(3L);
		when(paymentRequest.getTargetUserId()).thenReturn(4L);
		when(paymentRequest.getStatus()).thenReturn(status);
	}
}
