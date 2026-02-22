package com.dnd.moddo.event.application.query;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.dnd.moddo.event.application.impl.SettlementReader;
import com.dnd.moddo.event.application.impl.SettlementValidator;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.domain.settlement.type.SettlementStatus;
import com.dnd.moddo.event.presentation.request.SearchSettlementListRequest;
import com.dnd.moddo.event.presentation.response.SettlementDetailResponse;
import com.dnd.moddo.event.presentation.response.SettlementHeaderResponse;
import com.dnd.moddo.event.presentation.response.SettlementListResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuerySettlementService {
	private final SettlementReader settlementReader;
	private final SettlementValidator settlementValidator;

	public SettlementDetailResponse findOne(Long settlementId, Long userId) {
		Settlement settlement = settlementReader.read(settlementId);
		settlementValidator.checkSettlementAuthor(settlement, userId);
		List<Member> members = settlementReader.findBySettlement(settlementId);
		return SettlementDetailResponse.of(settlement, members);
	}

	public SettlementHeaderResponse findBySettlementHeader(Long settlementId) {
		return settlementReader.findByHeader(settlementId);
	}

	@Cacheable(cacheNames = "settlements", key = "#code")
	public Long findIdByCode(String code) {
		return settlementReader.findIdByGroupCode(code);
	}

	public Long findIdByCodeNoCache(String code) {
		return settlementReader.findIdByGroupCode(code);
	}

	public List<SettlementListResponse> search(
		Long userId,
		SearchSettlementListRequest request
	) {
		SettlementStatus effectiveStatus =
			request.status() == null ? SettlementStatus.ALL : request.status();

		int limit = request.limit() == null ? 10 : request.limit();

		return settlementReader.findListByUserIdAndStatus(userId, effectiveStatus, request.sort(), limit);
	}
}
