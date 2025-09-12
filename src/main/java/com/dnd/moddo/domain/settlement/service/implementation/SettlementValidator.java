package com.dnd.moddo.domain.settlement.service.implementation;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.settlement.dto.request.SettlementPasswordRequest;
import com.dnd.moddo.domain.settlement.dto.response.SettlementPasswordResponse;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.settlement.exception.GroupNotAuthorException;
import com.dnd.moddo.domain.settlement.exception.InvalidPasswordException;
import com.dnd.moddo.domain.settlement.repository.SettlementRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettlementValidator {

	private final PasswordEncoder passwordEncoder;

	private final SettlementRepository settlementRepository;

	public void checkSettlementAuthor(Settlement settlement, Long userId) {
		if (!settlement.isWriter(userId)) {
			throw new GroupNotAuthorException();
		}
	}

	public SettlementPasswordResponse checkSettlementPassword(SettlementPasswordRequest settlementPasswordRequest,
		String getPassword) {
		boolean isMatch = passwordEncoder.matches(settlementPasswordRequest.password(), getPassword);

		if (!isMatch) {
			throw new InvalidPasswordException();
		}

		return SettlementPasswordResponse.from("확인되었습니다.");
	}
}
