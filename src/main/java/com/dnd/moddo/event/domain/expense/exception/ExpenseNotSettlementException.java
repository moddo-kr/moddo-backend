package com.dnd.moddo.event.domain.expense.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class ExpenseNotSettlementException extends ModdoException {
	public ExpenseNotSettlementException() {
		super(HttpStatus.BAD_REQUEST, "해당 정산의 지출 내역이 아닙니다.");
	}
}
