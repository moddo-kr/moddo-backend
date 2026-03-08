package com.dnd.moddo.event.infrastructure;

import java.util.List;
import java.util.Map;

import com.dnd.moddo.event.presentation.response.MemberResponse;

public interface MemberQueryRepository {
	Map<Long, List<MemberResponse>> findMembersByIds(List<Long> settlementIds);
}
