package com.dnd.moddo.event.domain.member;

import java.time.LocalDateTime;
import java.util.Objects;

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
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
	name = "members",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_members_settlement_user", columnNames = {"settlement_id", "user_id"})
	}
)
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
		Objects.requireNonNull(user, "연결할 사용자는 필수입니다.");
		if (this.user != null) {
			throw new IllegalStateException("이미 사용자와 연결된 멤버입니다.");
		}
		this.user = user;
	}

	public void unassignUser(Long userId) {
		if (this.user == null) {
			throw new IllegalStateException("연결된 사용자가 없는 멤버입니다.");
		}
		if (!isAssignedTo(userId)) {
			throw new IllegalStateException("본인이 선택한 참여자만 해제할 수 있습니다.");
		}
		this.user = null;
	}

	public boolean isManager() {
		return ExpenseRole.MANAGER.equals(role);
	}

	public boolean isAssigned() {
		return user != null;
	}

	public boolean isAssignedTo(Long userId) {
		return getUserId() != null && getUserId().equals(userId);
	}

	public boolean isInSettlement(Long settlementId) {
		return settlement.getId().equals(settlementId);
	}

	public Long getUserId() {
		if (user == null) {
			return null;
		}
		return user.getId();
	}

	public void updatePaymentStatus(Boolean isPaid) {
		this.isPaid = isPaid;
		this.paidAt = isPaid ? LocalDateTime.now() : null;
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
