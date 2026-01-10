package com.dnd.moddo.event.application.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.application.impl.SettlementCreator;
import com.dnd.moddo.event.application.impl.SettlementReader;
import com.dnd.moddo.event.application.impl.SettlementUpdater;
import com.dnd.moddo.event.application.impl.SettlementValidator;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.presentation.request.SettlementAccountRequest;
import com.dnd.moddo.event.presentation.request.SettlementPasswordRequest;
import com.dnd.moddo.event.presentation.request.SettlementRequest;
import com.dnd.moddo.event.presentation.response.MemberResponse;
import com.dnd.moddo.event.presentation.response.SettlementPasswordResponse;
import com.dnd.moddo.event.presentation.response.SettlementResponse;
import com.dnd.moddo.event.presentation.response.SettlementSaveResponse;
import com.dnd.moddo.global.jwt.utill.JwtProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandSettlementService {
	private final SettlementCreator settlementCreator;
	private final SettlementUpdater settlementUpdater;
	private final SettlementValidator settlementValidator;
	private final SettlementReader settlementReader;
	private final JwtProvider jwtProvider;
	private final CommandMemberService commandMemberService;

	public SettlementSaveResponse createSettlement(SettlementRequest request, Long userId) {
		Settlement settlement = settlementCreator.createSettlement(request, userId);
		MemberResponse manager = commandMemberService.createManager(settlement, userId);
		return new SettlementSaveResponse(settlement.getCode(), manager);
	}

	public SettlementResponse updateAccount(SettlementAccountRequest request, Long userId, Long settlementId) {
		Settlement settlement = settlementReader.read(settlementId);
		settlementValidator.checkSettlementAuthor(settlement, userId);
		settlement = settlementUpdater.updateAccount(request, settlement.getId());
		return SettlementResponse.of(settlement);
	}

	public SettlementPasswordResponse isPasswordMatch(Long settlementId, Long userId,
		SettlementPasswordRequest request) {
		Settlement settlement = settlementReader.read(settlementId);
		settlementValidator.checkSettlementAuthor(settlement, userId);
		SettlementPasswordResponse response = settlementValidator.checkSettlementPassword(request,
			settlement.getPassword());
		return response;
	}
}
