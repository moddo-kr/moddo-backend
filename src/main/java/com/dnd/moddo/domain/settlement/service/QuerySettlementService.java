package com.dnd.moddo.domain.settlement.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.settlement.dto.response.GroupDetailResponse;
import com.dnd.moddo.domain.settlement.dto.response.GroupHeaderResponse;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.settlement.service.implementation.SettlementReader;
import com.dnd.moddo.domain.settlement.service.implementation.SettlementValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuerySettlementService {
	private final SettlementReader settlementReader;
	private final SettlementValidator settlementValidator;

	public GroupDetailResponse findOne(Long groupId, Long userId) {
		Settlement settlement = settlementReader.read(groupId);
		settlementValidator.checkGroupAuthor(settlement, userId);
		List<AppointmentMember> members = settlementReader.findByGroup(groupId);
		return GroupDetailResponse.of(settlement, members);
	}

	public GroupHeaderResponse findBySettlementHeader(Long groupId) {
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
