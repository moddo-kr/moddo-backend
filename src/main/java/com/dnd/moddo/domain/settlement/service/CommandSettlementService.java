package com.dnd.moddo.domain.settlement.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMemberResponse;
import com.dnd.moddo.domain.appointmentMember.service.CommandAppointmentMemberService;
import com.dnd.moddo.domain.settlement.dto.request.SettlementAccountRequest;
import com.dnd.moddo.domain.settlement.dto.request.SettlementPasswordRequest;
import com.dnd.moddo.domain.settlement.dto.request.SettlementRequest;
import com.dnd.moddo.domain.settlement.dto.response.SettlementPasswordResponse;
import com.dnd.moddo.domain.settlement.dto.response.SettlementResponse;
import com.dnd.moddo.domain.settlement.dto.response.SettlementSaveResponse;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.settlement.service.implementation.SettlementCreator;
import com.dnd.moddo.domain.settlement.service.implementation.SettlementReader;
import com.dnd.moddo.domain.settlement.service.implementation.SettlementUpdater;
import com.dnd.moddo.domain.settlement.service.implementation.SettlementValidator;
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
	private final CommandAppointmentMemberService commandAppointmentMemberService;

	public SettlementSaveResponse createSettlement(SettlementRequest request, Long userId) {
		Settlement settlement = settlementCreator.createSettlement(request, userId);
		AppointmentMemberResponse manager = commandAppointmentMemberService.createManager(settlement, userId);
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
