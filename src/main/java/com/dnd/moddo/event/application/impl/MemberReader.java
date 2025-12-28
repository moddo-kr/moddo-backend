package com.dnd.moddo.event.application.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.infrastructure.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberReader {
	private final MemberRepository memberRepository;

	public List<Member> findAllBySettlementId(Long settlementId) {
		return memberRepository.findBySettlementId(settlementId);
	}

	public Member findByAppointmentMemberId(Long appointmentMemberId) {
		return memberRepository.getById(appointmentMemberId);
	}

	public List<Long> findIdsBySettlementId(Long settlementId) {
		return memberRepository.findAppointmentMemberIdsBySettlementId(settlementId);
	}

}
