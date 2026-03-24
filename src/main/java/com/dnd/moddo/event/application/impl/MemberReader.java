package com.dnd.moddo.event.application.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.member.exception.MemberNotFoundException;
import com.dnd.moddo.event.domain.member.type.MemberSortType;
import com.dnd.moddo.event.infrastructure.MemberQueryRepository;
import com.dnd.moddo.event.infrastructure.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberReader {
	private final MemberRepository memberRepository;
	private final MemberQueryRepository memberQueryRepository;

	public List<Member> findAllBySettlementId(Long settlementId) {
		return findAllBySettlementId(settlementId, MemberSortType.CREATED);
	}

	public List<Member> findAllBySettlementId(Long settlementId, MemberSortType sortType) {
		return memberQueryRepository.findAllBySettlementId(settlementId, sortType);
	}

	public Member findByAppointmentMemberId(Long appointmentMemberId) {
		return memberRepository.getById(appointmentMemberId);
	}

	public List<Long> findIdsBySettlementId(Long settlementId) {
		return memberRepository.findMemberIdsBySettlementId(settlementId);
	}

	public Member findBySettlementIdAndUserId(Long settlementId, Long userId) {
		return memberRepository.findBySettlementIdAndUserId(settlementId, userId)
			.orElseThrow(() -> new MemberNotFoundException(userId));
	}

	public boolean existsUnpaidMember(Long settlementId) {
		return memberRepository.existsBySettlementIdAndIsPaidFalse(settlementId);
	}

	public List<Member> findAssignedMembersBySettlementId(Long settlementId) {
		return findAllBySettlementId(settlementId).stream()
			.filter(Member::isAssigned)
			.toList();
	}

}
