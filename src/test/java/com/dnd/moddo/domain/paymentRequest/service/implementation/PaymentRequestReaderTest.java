package com.dnd.moddo.domain.paymentRequest.service.implementation;

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

import com.dnd.moddo.event.application.impl.MemberExpenseReader;
import com.dnd.moddo.event.application.impl.PaymentRequestReader;
import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.memberExpense.MemberExpense;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequest;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.infrastructure.PaymentRequestRepository;
import com.dnd.moddo.event.presentation.response.PaymentRequestItemResponse;
import com.dnd.moddo.event.presentation.response.PaymentRequestsResponse;

@ExtendWith(MockitoExtension.class)
class PaymentRequestReaderTest {

	@Mock
	private PaymentRequestRepository paymentRequestRepository;

	@Mock
	private MemberExpenseReader memberExpenseReader;

	@InjectMocks
	private PaymentRequestReader paymentRequestReader;

	@Test
	@DisplayName("대상 유저 기준으로 입금 확인 요청 목록을 요청 시각 역순으로 조회할 수 있다.")
	void findByTargetUserId() {
		Settlement settlement = mock(Settlement.class);
		Member member1 = Member.builder()
			.name("김반숙")
			.profileId(1)
			.settlement(settlement)
			.role(ExpenseRole.PARTICIPANT)
			.build();
		Member member2 = Member.builder()
			.name("김모또")
			.profileId(2)
			.settlement(settlement)
			.role(ExpenseRole.PARTICIPANT)
			.build();

		PaymentRequest paymentRequest1 = PaymentRequest.builder()
			.settlement(settlement)
			.requestMember(member1)
			.targetUser(mock(com.dnd.moddo.user.domain.User.class))
			.build();
		PaymentRequest paymentRequest2 = PaymentRequest.builder()
			.settlement(settlement)
			.requestMember(member2)
			.targetUser(mock(com.dnd.moddo.user.domain.User.class))
			.build();

		setField(paymentRequest1, "id", 1L);
		setField(paymentRequest2, "id", 2L);
		setField(paymentRequest1, "requestedAt", LocalDateTime.of(2026, 3, 13, 21, 0));
		setField(paymentRequest2, "requestedAt", LocalDateTime.of(2026, 3, 13, 22, 0));
		setField(paymentRequest1, "status", PaymentRequestStatus.PENDING);
		setField(paymentRequest2, "status", PaymentRequestStatus.PENDING);
		setField(member1, "id", 11L);
		setField(member2, "id", 12L);

		List<MemberExpense> memberExpenses = List.of(
			MemberExpense.builder().expenseId(1L).member(member1).amount(3000L).build(),
			MemberExpense.builder().expenseId(2L).member(member1).amount(2000L).build(),
			MemberExpense.builder().expenseId(3L).member(member2).amount(7000L).build()
		);

		when(paymentRequestRepository.findByTargetUserId(1L)).thenReturn(List.of(paymentRequest1, paymentRequest2));
		when(memberExpenseReader.findAllByMemberIds(List.of(11L, 12L))).thenReturn(memberExpenses);

		PaymentRequestsResponse result = paymentRequestReader.findByTargetUserId(1L);

		assertThat(result.paymentRequests()).hasSize(2);
		PaymentRequestItemResponse first = result.paymentRequests().get(0);
		PaymentRequestItemResponse second = result.paymentRequests().get(1);

		assertThat(first.paymentRequestId()).isEqualTo(2L);
		assertThat(first.name()).isEqualTo("김모또");
		assertThat(first.profileUrl()).isEqualTo("https://moddo-s3.s3.amazonaws.com/profile/2.png");
		assertThat(first.totalAmount()).isEqualTo(7000L);

		assertThat(second.paymentRequestId()).isEqualTo(1L);
		assertThat(second.name()).isEqualTo("김반숙");
		assertThat(second.profileUrl()).isEqualTo("https://moddo-s3.s3.amazonaws.com/profile/1.png");
		assertThat(second.totalAmount()).isEqualTo(5000L);
	}

	private void setField(Object target, String fieldName, Object value) {
		try {
			java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(target, value);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}
}
