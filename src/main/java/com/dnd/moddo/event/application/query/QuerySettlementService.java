package com.dnd.moddo.event.application.query;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.common.cache.CacheExecutor;
import com.dnd.moddo.common.cache.CacheKeys;
import com.dnd.moddo.event.application.impl.SettlementReader;
import com.dnd.moddo.event.application.impl.SettlementValidator;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.domain.settlement.type.SettlementSortType;
import com.dnd.moddo.event.domain.settlement.type.SettlementStatus;
import com.dnd.moddo.event.presentation.request.SearchSettlementListRequest;
import com.dnd.moddo.event.presentation.response.SettlementDetailResponse;
import com.dnd.moddo.event.presentation.response.SettlementHeaderResponse;
import com.dnd.moddo.event.presentation.response.SettlementListResponse;
import com.dnd.moddo.event.presentation.response.SettlementShareResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuerySettlementService {
	private static final Duration SETTLEMENT_ID_CACHE_TTL = Duration.ofMinutes(10);
	private static final Duration SETTLEMENT_HEADER_CACHE_TTL = Duration.ofMinutes(5);
	private static final Duration SETTLEMENT_LIST_CACHE_TTL = Duration.ofMinutes(5);

	private final SettlementReader settlementReader;
	private final SettlementValidator settlementValidator;
	private final CacheExecutor cacheExecutor;

	public SettlementDetailResponse findOne(Long settlementId, Long userId) {
		Settlement settlement = settlementReader.read(settlementId);
		settlementValidator.checkSettlementAuthor(settlement, userId);
		List<Member> members = settlementReader.findBySettlement(settlementId);
		return SettlementDetailResponse.of(settlement, members);
	}

	public SettlementHeaderResponse findBySettlementHeader(Long settlementId) {
		return cacheExecutor.execute(
			CacheKeys.settlementHeader(settlementId),
			SETTLEMENT_HEADER_CACHE_TTL,
			() -> settlementReader.findByHeader(settlementId)
		);
	}

	public Long findIdByCode(String code) {
		Number settlementId = cacheExecutor.execute(
			CacheKeys.settlementCode(code),
			SETTLEMENT_ID_CACHE_TTL,
			() -> settlementReader.findIdByGroupCode(code)
		);
		return settlementId == null ? null : settlementId.longValue();
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

		SettlementSortType effectiveSort =
			request.sort() == null ? SettlementSortType.LATEST : request.sort();

		int limit = request.limit() == null ? 10 : request.limit();

		return settlementReader.findListByUserIdAndStatus(userId, effectiveStatus, effectiveSort, limit);
	}

	public List<SettlementShareResponse> findSettlementList(Long userId) {
		return cacheExecutor.execute(
			CacheKeys.settlementList(userId),
			SETTLEMENT_LIST_CACHE_TTL,
			() -> settlementReader.findSettlementListByUserId(userId)
		);
	}
}
