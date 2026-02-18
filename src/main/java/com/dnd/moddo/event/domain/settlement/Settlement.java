package com.dnd.moddo.event.domain.settlement;

import java.time.LocalDateTime;

import com.dnd.moddo.event.presentation.request.SettlementAccountRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "`settlement`")
public class Settlement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Long writer;

	private String name;

	private LocalDateTime createdAt;

	private LocalDateTime expiredAt;

	private LocalDateTime completedAt;

	private String bank;

	private String accountNumber;

	private LocalDateTime deadline;

	private Long characterId;

	@Column(unique = true)
	private String code;

	@Builder
	public Settlement(String name, Long writer, LocalDateTime createdAt,
		String bank, String accountNumber, String code, LocalDateTime deadline) {
		this.name = name;
		this.writer = writer;
		this.createdAt = createdAt;
		this.expiredAt = LocalDateTime.now().plusMonths(1);
		this.bank = bank;
		this.accountNumber = accountNumber;
		this.code = code;
		this.deadline = deadline;
	}

	public void updateAccount(SettlementAccountRequest request) {
		this.bank = request.bank();
		this.accountNumber = request.accountNumber();
		this.deadline = LocalDateTime.now().plusDays(1);
	}

	public void updateCharacter(Long characterId) {
		this.characterId = characterId;
	}

	public boolean isWriter(Long userId) {
		return this.writer.equals(userId);
	}

	public void complete() {
		this.completedAt = LocalDateTime.now();
	}
}
