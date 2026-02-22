package com.dnd.moddo.event.domain.member;

import java.time.LocalDateTime;

import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.user.domain.User;

import jakarta.persistence.Column;
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
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "members")
@Entity
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", updatable = false, nullable = false)
	private String name;

	@Column(name = "profile_id", nullable = false)
	private Integer profileId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "settlement_id", nullable = false)
	private Settlement settlement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = true)
	private User user;

	@Column(name = "is_paid", nullable = false)
	private boolean isPaid;

	@Column(name = "paid_at")
	private LocalDateTime paidAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ExpenseRole role;

	@Version
	private Long version = 0L;

	@Builder
	public Member(String name, Integer profileId, Settlement settlement, boolean isPaid, User user, ExpenseRole role) {
		this.name = name;
		this.profileId = profileId;
		this.settlement = settlement;
		this.role = role;
		this.user = user;
		this.isPaid = isPaid;
	}

	public void assignUser(User user) {
		if (this.user != null) {
			throw new IllegalStateException("이미 사용자와 연결된 멤버입니다.");
		}
		this.user = user;
		this.name = user.getName(); // 동기화
	}

	public boolean isManager() {
		return ExpenseRole.MANAGER.equals(role);
	}

	public void updatePaymentStatus(Boolean isPaid) {
		this.isPaid = isPaid;
		this.paidAt = Boolean.TRUE.equals(isPaid) ? LocalDateTime.now() : null;
	}

	public Long getSettlementId() {
		return settlement.getId();
	}

	public String getProfileUrl() {
		if (profileId == 0) {
			return "https://moddo-s3.s3.amazonaws.com/profile/MODDO.png";
		}
		return "https://moddo-s3.s3.amazonaws.com/profile/" + profileId + ".png";
	}
}