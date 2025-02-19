package com.dnd.moddo.domain.group.dto.response;

import com.dnd.moddo.domain.groupMember.dto.response.GroupMemberResponse;

public record GroupSaveResponse(
	String groupToken,
	GroupMemberResponse manager
) {
}
