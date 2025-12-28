package com.dnd.moddo.event.presentation.response;

import java.util.List;

public record MembersExpenseResponse(
	List<MemberExpenseItemResponse> memberExpenses
) {

}
