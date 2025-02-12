package com.dnd.moddo.domain.expense.dto.request;

import java.util.List;

public record ExpensesUpdateOrderRequest(
	List<ExpenseUpdateOrderRequest> orders
) {
}
