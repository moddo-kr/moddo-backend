package com.dnd.moddo.event.application.query;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.common.cache.CacheExecutor;
import com.dnd.moddo.common.cache.CacheKeys;
import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.member.type.MemberSortType;
import com.dnd.moddo.event.presentation.response.MembersResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QueryMemberService {
	private static final Duration MEMBERS_CACHE_TTL = Duration.ofMinutes(5);

	private final MemberReader memberReader;
	private final CacheExecutor cacheExecutor;

	public MembersResponse findAll(Long settlementId, MemberSortType sortType) {
		List<Member> members = cacheExecutor.execute(
			CacheKeys.members(settlementId, sortType),
			MEMBERS_CACHE_TTL,
			() -> memberReader.findAllBySettlementId(settlementId, sortType)
		);
		return MembersResponse.of(members);
	}

	public List<Member> findAllBySettlementId(Long settlementId) {
		return memberReader.findAllBySettlementId(settlementId);
	}
}
