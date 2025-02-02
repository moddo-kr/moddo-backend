package com.dnd.moddo.domain.expense.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.global.exception.ModdoException;

public class ExpenseNotFoundException extends ModdoException {
	public ExpenseNotFoundException(Long expenseId) {
		super(HttpStatus.NOT_FOUND, expenseId + " 의 지출내역을 찾을 수 없습니다.");
	}
}
