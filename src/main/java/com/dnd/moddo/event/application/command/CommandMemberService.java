package com.dnd.moddo.event.application.command;

import org.springframework.stereotype.Service;

import com.dnd.moddo.event.application.impl.SettlementCompletionProcessor;
import com.dnd.moddo.event.application.impl.MemberCreator;
import com.dnd.moddo.event.application.impl.MemberDeleter;
import com.dnd.moddo.event.application.impl.MemberUpdater;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.presentation.request.MemberSaveRequest;
import com.dnd.moddo.event.presentation.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.event.presentation.response.MemberResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandMemberService {
	private final MemberCreator memberCreator;
	private final MemberUpdater memberUpdater;
	private final MemberDeleter memberDeleter;
	private final SettlementCompletionProcessor settlementCompletionProcessor;

	public MemberResponse createManager(Settlement settlement, Long userId) {
		Member member = memberCreator.createManagerForSettlement(settlement, userId);
		return MemberResponse.of(member);
	}

	public MemberResponse addMember(Long settlementId, MemberSaveRequest request) {
		Member member = memberUpdater.addToSettlement(settlementId, request);
		return MemberResponse.of(member);
	}

	public MemberResponse updatePaymentStatus(Long appointmentMemberId, PaymentStatusUpdateRequest request) {
		Member member = memberUpdater.updatePaymentStatus(appointmentMemberId,
			request);
		settlementCompletionProcessor.completeIfAllPaid(member.getSettlementId());
		return MemberResponse.of(member);
	}

	public MemberResponse assignMember(Long settlementId, Long memberId, Long userId) {
		Member member = memberUpdater.assignMember(settlementId, memberId, userId);
		return MemberResponse.of(member);
	}

	public MemberResponse unassignMember(Long settlementId, Long memberId, Long userId) {
		Member member = memberUpdater.unassignMember(settlementId, memberId, userId);
		return MemberResponse.of(member);
	}

	public void delete(Long appointmentMemberId) {
		memberDeleter.delete(appointmentMemberId);
	}

}
