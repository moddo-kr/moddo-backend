package com.dnd.moddo.domain.expense.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.dnd.moddo.domain.expense.entity.Expense;

import lombok.Builder;

@Builder
public record ExpenseDetailResponse(
	Long id,
	LocalDate date,
	String content,
	Long totalAmount,
	List<String> groupMembers
) {
	public static ExpenseDetailResponse of(Expense expense, List<String> groupMembers) {
		return ExpenseDetailResponse.builder()
			.id(expense.getId())
			.date(expense.getDate())
			.content(expense.getContent())
			.totalAmount(expense.getAmount())
			.groupMembers(groupMembers)
			.build();
	}

}
