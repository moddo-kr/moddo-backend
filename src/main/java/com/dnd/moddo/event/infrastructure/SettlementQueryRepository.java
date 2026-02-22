package com.dnd.moddo.event.infrastructure;

import java.util.List;

import com.dnd.moddo.event.domain.settlement.type.SettlementSortType;
import com.dnd.moddo.event.domain.settlement.type.SettlementStatus;
import com.dnd.moddo.event.presentation.response.SettlementListResponse;
import com.dnd.moddo.event.presentation.response.SettlementShareResponse;

public interface SettlementQueryRepository {
	List<SettlementListResponse> findByUserAndStatus(
		Long userId,
		SettlementStatus status,
		SettlementSortType sortType,
		int limit
	);

	List<SettlementShareResponse> findBySettlementList(Long userId);
}
