package com.dnd.moddo.common.cache;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.domain.member.Member;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CacheEvictor {
	private final CacheExecutor cacheExecutor;
	private final MemberReader memberReader;

	public void evictSettlementList(Long userId) {
		runAfterCommit(() -> cacheExecutor.evict(CacheKeys.settlementList(userId)));
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
		runAfterCommit(() -> cacheExecutor.evict(CacheKeys.settlementHeader(settlementId)));
	}

	public void evictMembers(Long settlementId) {
		runAfterCommit(() -> cacheExecutor.evictByPrefix(CacheKeys.membersPrefix(settlementId)));
	}

	public void evictCollections(Long userId) {
		runAfterCommit(() -> cacheExecutor.evict(CacheKeys.collections(userId)));
	}

	private void runAfterCommit(Runnable action) {
		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			action.run();
			return;
		}

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				action.run();
			}
		});
	}
}
