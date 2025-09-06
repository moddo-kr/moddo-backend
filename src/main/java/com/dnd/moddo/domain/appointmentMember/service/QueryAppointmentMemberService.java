package com.dnd.moddo.domain.appointmentMember.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMembersResponse;
import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.service.implementation.AppointmentMemberReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QueryAppointmentMemberService {
	private final AppointmentMemberReader appointmentMemberReader;

	public AppointmentMembersResponse findAll(Long settlementId) {
		List<AppointmentMember> members = appointmentMemberReader.findAllBySettlementId(settlementId);
		return AppointmentMembersResponse.of(members);
	}
}
