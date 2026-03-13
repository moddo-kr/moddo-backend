package com.dnd.moddo.event.application.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.member.exception.InvalidMemberException;
import com.dnd.moddo.event.domain.member.exception.MemberAlreadyAssignedException;
import com.dnd.moddo.event.domain.member.exception.MemberNotAssignedException;
import com.dnd.moddo.event.domain.member.exception.MemberSelectionNotAllowedException;
import com.dnd.moddo.event.domain.member.exception.MemberSelectionUnauthorizedException;
import com.dnd.moddo.event.domain.member.exception.PaymentConcurrencyException;
import com.dnd.moddo.event.domain.member.exception.UserAlreadyAssignedException;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.infrastructure.MemberRepository;
import com.dnd.moddo.event.presentation.request.MemberSaveRequest;
import com.dnd.moddo.event.presentation.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.user.domain.User;
import com.dnd.moddo.user.infrastructure.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberUpdater {
	private final MemberRepository memberRepository;
	private final MemberReader memberReader;
	private final MemberValidator memberValidator;
	private final SettlementReader settlementReader;
	private final UserRepository userRepository;

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
		return updatePaymentStatus(appointmentMemberId, request.isPaid());
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Member updatePaymentStatus(Long appointmentMemberId, boolean isPaid) {
		try {
			Member member = memberRepository.getById(appointmentMemberId);
			if (member.isPaid() != isPaid) {
				member.updatePaymentStatus(isPaid);
				memberRepository.save(member);
			}
			return member;
		} catch (OptimisticLockingFailureException e) {
			throw new PaymentConcurrencyException();
		}
	}

	@Transactional
	public Member assignMember(Long settlementId, Long memberId, Long userId) {
		Member member = memberRepository.getById(memberId);
		validateMemberBelongsToSettlement(member, settlementId);
		validateSelectable(member);

		if (memberRepository.existsBySettlementIdAndUserId(settlementId, userId)) {
			throw new UserAlreadyAssignedException(userId);
		}
		if (member.isAssigned()) {
			throw new MemberAlreadyAssignedException(memberId);
		}

		User user = userRepository.getById(userId);
		member.assignUser(user);
		return memberRepository.save(member);
	}

	@Transactional
	public Member unassignMember(Long settlementId, Long memberId, Long userId) {
		Member member = memberRepository.getById(memberId);
		validateMemberBelongsToSettlement(member, settlementId);
		validateSelectable(member);
		if (!member.isAssigned()) {
			throw new MemberNotAssignedException(memberId);
		}
		if (!member.isAssignedTo(userId)) {
			throw new MemberSelectionUnauthorizedException(memberId);
		}

		member.unassignUser(userId);
		return memberRepository.save(member);
	}

	private Integer findAvailableProfileId(List<Integer> usedProfiles) {
		for (int i = 1; i <= 8; i++) {
			if (!usedProfiles.contains(i)) {
				return i;
			}
		}

		return (usedProfiles.size() % 8) + 1;
	}

	private void validateMemberBelongsToSettlement(Member member, Long settlementId) {
		if (!member.isInSettlement(settlementId)) {
			throw new InvalidMemberException(member.getId());
		}
	}

	private void validateSelectable(Member member) {
		if (member.isManager()) {
			throw new MemberSelectionNotAllowedException(member.getId());
		}
	}
}
