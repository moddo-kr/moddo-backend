package com.dnd.moddo.event.presentation.request;

import com.dnd.moddo.event.domain.settlement.type.SettlementSortType;
import com.dnd.moddo.event.domain.settlement.type.SettlementStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SearchSettlementListRequest(

	@NotNull(message = "정산 상태는 필수입니다.")
	SettlementStatus status,

	@NotNull(message = "정렬 방식은 필수입니다.")
	SettlementSortType sort,

	@Min(value = 1, message = "limit은 1 이상이어야 합니다.")
	@Max(value = 100, message = "limit은 최대 100까지 가능합니다.")
	Integer limit
) {
}