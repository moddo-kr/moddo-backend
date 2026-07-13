package com.dnd.moddo.event.domain.expense.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class ExpenseModificationLockedException extends ModdoException {
	public ExpenseModificationLockedException(Long settlementId) {
		super(HttpStatus.FORBIDDEN, "입금 확인 요청 또는 입금 완료 상태가 있어 지출내역을 수정할 수 없습니다. (Settlement ID: " + settlementId + ")");
	}
}
