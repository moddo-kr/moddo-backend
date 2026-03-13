package com.dnd.moddo.event.infrastructure;

import java.util.List;
import java.util.Map;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.member.type.MemberSortType;
import com.dnd.moddo.event.presentation.response.MemberResponse;

public interface MemberQueryRepository {
	List<Member> findAllBySettlementId(Long settlementId, MemberSortType sortType);

	Map<Long, List<MemberResponse>> findMembersByIds(List<Long> settlementIds);
}
