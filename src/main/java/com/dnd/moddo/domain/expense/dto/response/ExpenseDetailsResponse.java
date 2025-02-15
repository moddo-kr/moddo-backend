package com.dnd.moddo.domain.expense.dto.response;

import java.util.List;

public record ExpenseDetailsResponse(
	List<ExpenseDetailResponse> expenses
) {
}
