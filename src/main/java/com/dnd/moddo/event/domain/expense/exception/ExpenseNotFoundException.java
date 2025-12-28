package com.dnd.moddo.event.domain.expense.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.global.exception.ModdoException;

public class ExpenseNotFoundException extends ModdoException {
	public ExpenseNotFoundException(Long expenseId) {
		super(HttpStatus.NOT_FOUND, "해당 지출내역을 찾을 수 없습니다. (Expense ID: " + expenseId + ")");
	}
}
