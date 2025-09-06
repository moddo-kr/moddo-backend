package com.dnd.moddo.domain.settlement.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.settlement.dto.response.SettlementDetailResponse;
import com.dnd.moddo.domain.settlement.dto.response.SettlementHeaderResponse;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.settlement.service.implementation.SettlementReader;
import com.dnd.moddo.domain.settlement.service.implementation.SettlementValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuerySettlementService {
	private final SettlementReader settlementReader;
	private final SettlementValidator settlementValidator;

	public SettlementDetailResponse findOne(Long groupId, Long userId) {
		Settlement settlement = settlementReader.read(groupId);
		settlementValidator.checkSettlementAuthor(settlement, userId);
		List<AppointmentMember> members = settlementReader.findBySettlement(groupId);
		return SettlementDetailResponse.of(settlement, members);
	}

	public SettlementHeaderResponse findBySettlementHeader(Long groupId) {
		return settlementReader.findByHeader(groupId);
	}

	@Cacheable(cacheNames = "settlements", key = "#code")
	public Long findIdByCode(String code) {
		return settlementReader.findIdByGroupCode(code);
	}

	public Long findIdByCodeNoCache(String code) {
		return settlementReader.findIdByGroupCode(code);
	}
}
