package com.dnd.moddo.domain.groupMember.dto.response;

import java.util.List;

public record GroupMembersExpenseResponse(
	List<GroupMemberExpenseResponse> memberExpenses
) {
	
}
