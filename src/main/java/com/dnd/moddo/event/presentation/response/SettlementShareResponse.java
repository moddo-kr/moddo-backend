package com.dnd.moddo.event.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SettlementShareResponse {

	private Long settlementId;
	private String name;
	private String groupCode;
	private LocalDateTime createdAt;
	private LocalDateTime completedAt;
	private List<MemberResponse> members;

	public SettlementShareResponse(Long settlementId, String name, String groupCode, LocalDateTime createdAt,
		LocalDateTime completedAt) {
		this.settlementId = settlementId;
		this.name = name;
		this.groupCode = groupCode;
		this.createdAt = createdAt;
		this.completedAt = completedAt;
	}
}