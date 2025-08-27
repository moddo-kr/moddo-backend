package com.dnd.moddo.domain.settlement.service.implementation;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.settlement.dto.request.GroupPasswordRequest;
import com.dnd.moddo.domain.settlement.dto.response.GroupPasswordResponse;
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

	public void checkGroupAuthor(Settlement settlement, Long userId) {
		if (!settlement.isWriter(userId)) {
			throw new GroupNotAuthorException();
		}
	}

	public GroupPasswordResponse checkGroupPassword(GroupPasswordRequest groupPasswordRequest, String getPassword) {
		boolean isMatch = passwordEncoder.matches(groupPasswordRequest.password(), getPassword);

		if (!isMatch) {
			throw new InvalidPasswordException();
		}

		return GroupPasswordResponse.from("확인되었습니다.");
	}
}
