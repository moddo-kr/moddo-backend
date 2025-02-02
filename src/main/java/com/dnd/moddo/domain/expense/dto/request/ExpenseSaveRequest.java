package com.dnd.moddo.domain.expense.dto.request;

import java.util.Date;
import java.util.List;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.memberExpense.dto.request.MemberExpenseRequest;

public record ExpenseSaveRequest(
	Double amount,
	String content,
	Date date,
	List<MemberExpenseRequest> memberExpenses

) {

	public Expense toEntity(Long meetId) {
		return new Expense(meetId, amount(), content(), date());
	}
}
