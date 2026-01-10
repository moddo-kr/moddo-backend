package com.dnd.moddo.event.application.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.member.exception.PaymentConcurrencyException;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.infrastructure.MemberRepository;
import com.dnd.moddo.event.presentation.request.MemberSaveRequest;
import com.dnd.moddo.event.presentation.request.PaymentStatusUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberUpdater {
	private final MemberRepository memberRepository;
	private final MemberReader memberReader;
	private final MemberValidator memberValidator;
	private final SettlementReader settlementReader;

	@Transactional
	public Member addToSettlement(Long settlementId, MemberSaveRequest request) {
		Settlement settlement = settlementReader.read(settlementId);
		List<Member> members = memberReader.findAllBySettlementId(settlementId);

		List<String> existingNames = new ArrayList<>(
			members.stream().map(Member::getName).toList());
		existingNames.add(request.name());

		memberValidator.validateMemberNamesNotDuplicate(existingNames);

		List<Integer> usedProfiles = members.stream()
			.filter(member -> !member.isManager())
			.map(Member::getProfileId)
			.toList();

		Integer newProfileId = findAvailableProfileId(usedProfiles);

		Member newMember = request.toEntity(settlement, newProfileId, ExpenseRole.PARTICIPANT);
		newMember = memberRepository.save(newMember);

		return newMember;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Member updatePaymentStatus(Long appointmentMemberId, PaymentStatusUpdateRequest request) {
		try {
			Member member = memberRepository.getById(appointmentMemberId);
			if (member.isPaid() != request.isPaid()) {
				member.updatePaymentStatus(request.isPaid());
				memberRepository.save(member);
			}
			return member;
		} catch (OptimisticLockingFailureException e) {
			throw new PaymentConcurrencyException();
		}
	}

	private Integer findAvailableProfileId(List<Integer> usedProfiles) {
		for (int i = 1; i <= 8; i++) {
			if (!usedProfiles.contains(i)) {
				return i;
			}
		}

		return (usedProfiles.size() % 8) + 1;
	}
}



