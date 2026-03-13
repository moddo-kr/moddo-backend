package com.dnd.moddo.domain.paymentRequest.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.application.impl.PaymentRequestCreator;
import com.dnd.moddo.event.application.impl.PaymentRequestValidator;
import com.dnd.moddo.event.application.impl.SettlementReader;
import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequest;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.infrastructure.PaymentRequestRepository;
import com.dnd.moddo.global.support.UserTestFactory;
import com.dnd.moddo.user.application.impl.UserReader;
import com.dnd.moddo.user.domain.User;

@ExtendWith(MockitoExtension.class)
class PaymentRequestCreatorTest {

	@Mock
	private PaymentRequestRepository paymentRequestRepository;

	@Mock
	private MemberReader memberReader;

	@Mock
	private SettlementReader settlementReader;

	@Mock
	private UserReader userReader;

	@Mock
	private PaymentRequestValidator paymentRequestValidator;

	@InjectMocks
	private PaymentRequestCreator paymentRequestCreator;

	@Test
	@DisplayName("입금 확인 요청을 생성할 수 있다.")
	void createPaymentRequestSuccess() {
		Long settlementId = 1L;
		Long userId = 10L;
		Long writerId = 99L;

		Member requestMember = Member.builder()
			.name("참여자")
			.profileId(1)
			.role(ExpenseRole.PARTICIPANT)
			.build();
		Settlement settlement = mock(Settlement.class);
		User targetUser = UserTestFactory.createWithEmail("writer@test.com");

		when(memberReader.findBySettlementIdAndUserId(settlementId, userId)).thenReturn(requestMember);
		when(settlementReader.read(settlementId)).thenReturn(settlement);
		when(settlement.getWriter()).thenReturn(writerId);
		when(userReader.read(writerId)).thenReturn(targetUser);
		when(paymentRequestRepository.save(any(PaymentRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

		PaymentRequest result = paymentRequestCreator.createPaymentRequest(settlementId, userId);

		assertThat(result).isNotNull();
		assertThat(result.getSettlement()).isEqualTo(settlement);
		assertThat(result.getRequestMember()).isEqualTo(requestMember);
		assertThat(result.getTargetUser()).isEqualTo(targetUser);
		assertThat(result.getStatus()).isEqualTo(PaymentRequestStatus.PENDING);

		verify(paymentRequestValidator).validateCreateRequest(settlementId, requestMember);
		verify(paymentRequestRepository).save(any(PaymentRequest.class));
	}
}
