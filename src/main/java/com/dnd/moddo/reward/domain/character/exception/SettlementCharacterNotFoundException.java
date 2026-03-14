package com.dnd.moddo.reward.domain.character.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class SettlementCharacterNotFoundException extends ModdoException {
	public SettlementCharacterNotFoundException(Long settlementId) {
		super(HttpStatus.NOT_FOUND, "정산에 연결된 캐릭터를 찾을 수 없습니다. settlementId=" + settlementId);
	}
}
