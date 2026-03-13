package com.dnd.moddo.domain.paymentRequest.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequest;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.global.support.GroupTestFactory;
import com.dnd.moddo.global.support.UserTestFactory;
import com.dnd.moddo.user.domain.User;

class PaymentRequestTest {

	@Test
	@DisplayName("입금 확인 요청을 생성하면 초기 상태는 PENDING이다.")
	void createPaymentRequest() {
		Settlement settlement = GroupTestFactory.createDefault();
		Member requestMember = createMember(settlement);
		User targetUser = UserTestFactory.createWithEmail("target@test.com");

		setField(settlement, "id", 1L);
		setField(requestMember, "id", 2L);
		setField(targetUser, "id", 3L);

		PaymentRequest paymentRequest = PaymentRequest.builder()
			.settlement(settlement)
			.requestMember(requestMember)
			.targetUser(targetUser)
			.build();

		assertThat(paymentRequest.getStatus()).isEqualTo(PaymentRequestStatus.PENDING);
		assertThat(paymentRequest.getRequestedAt()).isNotNull();
		assertThat(paymentRequest.getProcessedAt()).isNull();
	}

	@Test
	@DisplayName("입금 확인 요청을 승인하면 상태와 처리 시각이 변경된다.")
	void approve() {
		PaymentRequest paymentRequest = createPaymentRequestWithIds();

		paymentRequest.approve();

		assertThat(paymentRequest.getStatus()).isEqualTo(PaymentRequestStatus.APPROVED);
		assertThat(paymentRequest.getProcessedAt()).isNotNull();
	}

	@Test
	@DisplayName("입금 확인 요청을 거절하면 상태와 처리 시각이 변경된다.")
	void reject() {
		PaymentRequest paymentRequest = createPaymentRequestWithIds();

		paymentRequest.reject();

		assertThat(paymentRequest.getStatus()).isEqualTo(PaymentRequestStatus.REJECTED);
		assertThat(paymentRequest.getProcessedAt()).isNotNull();
	}

	@Test
	@DisplayName("이미 승인된 입금 확인 요청은 다시 승인할 수 없다.")
	void approveFailWhenAlreadyProcessed() {
		PaymentRequest paymentRequest = createPaymentRequestWithIds();
		paymentRequest.approve();

		assertThatThrownBy(paymentRequest::approve)
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("이미 처리된 입금 요청입니다.");
	}

	@Test
	@DisplayName("이미 거절된 입금 확인 요청은 다시 거절할 수 없다.")
	void rejectFailWhenAlreadyProcessed() {
		PaymentRequest paymentRequest = createPaymentRequestWithIds();
		paymentRequest.reject();

		assertThatThrownBy(paymentRequest::reject)
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("이미 처리된 입금 요청입니다.");
	}

	@Test
	@DisplayName("연관 엔티티의 식별자를 helper 메서드로 조회할 수 있다.")
	void helperGetters() {
		PaymentRequest paymentRequest = createPaymentRequestWithIds();

		assertThat(paymentRequest.getSettlementId()).isEqualTo(1L);
		assertThat(paymentRequest.getRequestMemberId()).isEqualTo(2L);
		assertThat(paymentRequest.getTargetUserId()).isEqualTo(3L);
	}

	private PaymentRequest createPaymentRequestWithIds() {
		Settlement settlement = GroupTestFactory.createDefault();
		Member requestMember = createMember(settlement);
		User targetUser = UserTestFactory.createWithEmail("target@test.com");

		setField(settlement, "id", 1L);
		setField(requestMember, "id", 2L);
		setField(targetUser, "id", 3L);

		return PaymentRequest.builder()
			.settlement(settlement)
			.requestMember(requestMember)
			.targetUser(targetUser)
			.build();
	}

	private Member createMember(Settlement settlement) {
		return Member.builder()
			.name("김반숙")
			.profileId(1)
			.settlement(settlement)
			.role(ExpenseRole.PARTICIPANT)
			.build();
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
