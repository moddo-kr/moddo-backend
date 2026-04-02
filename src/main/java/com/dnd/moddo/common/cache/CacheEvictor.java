package com.dnd.moddo.common.cache;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.domain.member.Member;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CacheEvictor {
	private final CacheExecutor cacheExecutor;
	private final MemberReader memberReader;

	public void evictSettlementList(Long userId) {
		cacheExecutor.evict(CacheKeys.settlementList(userId));
	}

	public void evictSettlementListsBySettlement(Long settlementId, Long... extraUserIds) {
		Set<Long> userIds = new LinkedHashSet<>();
		for (Member member : memberReader.findAllBySettlementId(settlementId)) {
			if (member.getUserId() != null) {
				userIds.add(member.getUserId());
			}
		}

		for (Long extraUserId : extraUserIds) {
			if (extraUserId != null) {
				userIds.add(extraUserId);
			}
		}

		for (Long userId : userIds) {
			evictSettlementList(userId);
		}
	}

	public void evictSettlementHeader(Long settlementId) {
		cacheExecutor.evict(CacheKeys.settlementHeader(settlementId));
	}

	public void evictMembers(Long settlementId) {
		cacheExecutor.evictByPrefix(CacheKeys.membersPrefix(settlementId));
	}

	public void evictCollections(Long userId) {
		cacheExecutor.evict(CacheKeys.collections(userId));
	}
}
