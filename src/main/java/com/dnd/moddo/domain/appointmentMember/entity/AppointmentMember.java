package com.dnd.moddo.domain.appointmentMember.entity;

import java.time.LocalDateTime;

import com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.settlement.entity.Settlement;

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
@Table(name = "appointment_members")
@Entity
public class AppointmentMember {

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
	public AppointmentMember(String name, Integer profileId, Settlement settlement, boolean isPaid, ExpenseRole role) {
		this.name = name;
		this.profileId = profileId;
		this.settlement = settlement;
		this.isPaid = isPaid;
		this.role = role;
	}

	public boolean isManager() {
		return ExpenseRole.MANAGER.equals(role);
	}

	public void updatePaymentStatus(Boolean isPaid) {
		this.isPaid = isPaid;
		this.paidAt = Boolean.TRUE.equals(isPaid) ? LocalDateTime.now() : null;
	}

	public String getProfileUrl() {
		if (profileId == 0) {
			return "https://moddo-s3.s3.amazonaws.com/profile/MODDO.png";
		}
		return "https://moddo-s3.s3.amazonaws.com/profile/" + profileId + ".png";
	}
}