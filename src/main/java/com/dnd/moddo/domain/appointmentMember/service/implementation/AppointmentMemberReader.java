package com.dnd.moddo.domain.appointmentMember.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.repository.AppointmentMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentMemberReader {
	private final AppointmentMemberRepository appointmentMemberRepository;

	public List<AppointmentMember> findAllBySettlementId(Long settlementId) {
		return appointmentMemberRepository.findBySettlementId(settlementId);
	}

	public AppointmentMember findByAppointmentMemberId(Long appointmentMemberId) {
		return appointmentMemberRepository.getById(appointmentMemberId);
	}

	public List<Long> findIdsBySettlementId(Long settlementId) {
		return appointmentMemberRepository.findAppointmentMemberIdsBySettlementId(settlementId);
	}

}
