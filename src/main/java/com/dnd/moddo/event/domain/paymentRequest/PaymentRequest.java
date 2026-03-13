package com.dnd.moddo.event.domain.paymentRequest;

import java.time.LocalDateTime;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.user.domain.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment_request")
@Entity
public class PaymentRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "settlement_id", nullable = false)
	private Settlement settlement;

	@ManyToOne
	@JoinColumn(name = "request_member_id", nullable = false)
	private Member requestMember;

	@ManyToOne
	@JoinColumn(name = "target_user_id", nullable = false)
	private User targetUser;

	private LocalDateTime requestedAt;

	private LocalDateTime processedAt;

	@Enumerated(EnumType.STRING)
	private PaymentRequestStatus status;

	@Builder
	public PaymentRequest(Settlement settlement, Member requestMember, User targetUser) {
		this.settlement = settlement;
		this.requestMember = requestMember;
		this.targetUser = targetUser;
		this.requestedAt = LocalDateTime.now();
		this.status = PaymentRequestStatus.PENDING;
	}

	private void assertPending() {
		if (this.status != PaymentRequestStatus.PENDING) {
			throw new IllegalStateException("이미 처리된 입금 요청입니다.");
		}
	}

	public void approve() {
		assertPending();
		this.status = PaymentRequestStatus.APPROVED;
		this.processedAt = LocalDateTime.now();
	}

	public void reject() {
		assertPending();
		this.status = PaymentRequestStatus.REJECTED;
		this.processedAt = LocalDateTime.now();
	}

	public Long getRequestMemberId() {
		return requestMember.getId();
	}

	public Long getSettlementId() {
		return settlement.getId();
	}

	public Long getTargetUserId() {
		return targetUser.getId();
	}

}
