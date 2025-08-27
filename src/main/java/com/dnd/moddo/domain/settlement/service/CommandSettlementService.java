package com.dnd.moddo.domain.settlement.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMemberResponse;
import com.dnd.moddo.domain.appointmentMember.service.CommandAppointmentMemberService;
import com.dnd.moddo.domain.settlement.dto.request.GroupPasswordRequest;
import com.dnd.moddo.domain.settlement.dto.request.SettlementAccountRequest;
import com.dnd.moddo.domain.settlement.dto.request.SettlementRequest;
import com.dnd.moddo.domain.settlement.dto.response.GroupPasswordResponse;
import com.dnd.moddo.domain.settlement.dto.response.GroupResponse;
import com.dnd.moddo.domain.settlement.dto.response.GroupSaveResponse;
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

	public GroupSaveResponse createSettlement(SettlementRequest request, Long userId) {
		Settlement settlement = settlementCreator.createSettlement(request, userId);
		AppointmentMemberResponse manager = commandAppointmentMemberService.createManager(settlement, userId);
		return new GroupSaveResponse(settlement.getCode(), manager);
	}

	public GroupResponse updateAccount(SettlementAccountRequest request, Long userId, Long groupId) {
		Settlement settlement = settlementReader.read(groupId);
		settlementValidator.checkGroupAuthor(settlement, userId);
		settlement = settlementUpdater.updateAccount(request, settlement.getId());
		return GroupResponse.of(settlement);
	}

	public GroupPasswordResponse isPasswordMatch(Long groupId, Long userId, GroupPasswordRequest request) {
		Settlement settlement = settlementReader.read(groupId);
		settlementValidator.checkGroupAuthor(settlement, userId);
		GroupPasswordResponse response = settlementValidator.checkGroupPassword(request, settlement.getPassword());
		return response;
	}
}
