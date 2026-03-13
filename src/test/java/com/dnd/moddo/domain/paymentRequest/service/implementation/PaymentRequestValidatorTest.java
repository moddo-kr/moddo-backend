package com.dnd.moddo.domain.paymentRequest.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.PaymentRequestValidator;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequest;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus;
import com.dnd.moddo.event.domain.paymentRequest.exception.DuplicatePendingPaymentRequestException;
import com.dnd.moddo.event.domain.paymentRequest.exception.ManagerPaymentRequestNotAllowedException;
import com.dnd.moddo.event.domain.paymentRequest.exception.PaymentRequestAlreadyApprovedException;
import com.dnd.moddo.event.domain.paymentRequest.exception.PaymentRequestNotPendingException;
import com.dnd.moddo.event.domain.paymentRequest.exception.PaymentRequestUnauthorizedException;
import com.dnd.moddo.event.infrastructure.PaymentRequestRepository;
import com.dnd.moddo.user.domain.User;

@ExtendWith(MockitoExtension.class)
class PaymentRequestValidatorTest {

	@Mock
	private PaymentRequestRepository paymentRequestRepository;

	@InjectMocks
	private PaymentRequestValidator paymentRequestValidator;

	@Test
	@DisplayName("총무는 입금 확인 요청을 생성할 수 없다.")
	void validateCreateRequestFailWhenManager() {
		Member requestMember = mock(Member.class);
		when(requestMember.isManager()).thenReturn(true);
		when(requestMember.getId()).thenReturn(1L);

		assertThatThrownBy(() -> paymentRequestValidator.validateCreateRequest(1L, requestMember))
			.isInstanceOf(ManagerPaymentRequestNotAllowedException.class);
	}

	@Test
	@DisplayName("대기 중인 동일 입금 확인 요청이 있으면 생성할 수 없다.")
	void validateCreateRequestFailWhenDuplicatePending() {
		Member requestMember = mock(Member.class);
		when(requestMember.isManager()).thenReturn(false);
		when(requestMember.getId()).thenReturn(2L);
		when(paymentRequestRepository.existsBySettlementIdAndRequestMemberIdAndStatus(1L, 2L, PaymentRequestStatus.PENDING))
			.thenReturn(true);

		assertThatThrownBy(() -> paymentRequestValidator.validateCreateRequest(1L, requestMember))
			.isInstanceOf(DuplicatePendingPaymentRequestException.class);
	}

	@Test
	@DisplayName("이미 승인된 입금 확인 요청이 있으면 생성할 수 없다.")
	void validateCreateRequestFailWhenAlreadyApproved() {
		Member requestMember = mock(Member.class);
		when(requestMember.isManager()).thenReturn(false);
		when(requestMember.getId()).thenReturn(2L);
		when(paymentRequestRepository.existsBySettlementIdAndRequestMemberIdAndStatus(1L, 2L, PaymentRequestStatus.PENDING))
			.thenReturn(false);
		when(paymentRequestRepository.existsBySettlementIdAndRequestMemberIdAndStatus(1L, 2L, PaymentRequestStatus.APPROVED))
			.thenReturn(true);

		assertThatThrownBy(() -> paymentRequestValidator.validateCreateRequest(1L, requestMember))
			.isInstanceOf(PaymentRequestAlreadyApprovedException.class);
	}

	@Test
	@DisplayName("처리 대상 유저가 아니면 승인 또는 거절할 수 없다.")
	void validateProcessRequestFailWhenUnauthorized() {
		PaymentRequest paymentRequest = mock(PaymentRequest.class);
		User targetUser = mock(User.class);

		when(paymentRequest.getStatus()).thenReturn(PaymentRequestStatus.PENDING);
		when(paymentRequest.getTargetUser()).thenReturn(targetUser);
		when(paymentRequest.getId()).thenReturn(1L);
		when(targetUser.getId()).thenReturn(100L);

		assertThatThrownBy(() -> paymentRequestValidator.validateProcessRequest(paymentRequest, 200L))
			.isInstanceOf(PaymentRequestUnauthorizedException.class);
	}

	@Test
	@DisplayName("대기 상태가 아니면 승인 또는 거절할 수 없다.")
	void validateProcessRequestFailWhenNotPending() {
		PaymentRequest paymentRequest = mock(PaymentRequest.class);

		when(paymentRequest.getStatus()).thenReturn(PaymentRequestStatus.APPROVED);
		when(paymentRequest.getId()).thenReturn(1L);

		assertThatThrownBy(() -> paymentRequestValidator.validateProcessRequest(paymentRequest, 100L))
			.isInstanceOf(PaymentRequestNotPendingException.class);
	}
}
