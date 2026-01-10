package com.dnd.moddo.event.application.command;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.event.application.impl.MemberCreator;
import com.dnd.moddo.event.application.impl.MemberDeleter;
import com.dnd.moddo.event.application.impl.MemberUpdater;
import com.dnd.moddo.event.application.query.QueryMemberService;
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

	private final QueryMemberService queryMemberService;

	public MemberResponse createManager(Settlement settlement, Long userId) {
		Member member = memberCreator.createManagerForSettlement(settlement, userId);
		return MemberResponse.of(member);
	}

	public MemberResponse addAppointmentMember(Long settlementId, MemberSaveRequest request) {
		Member member = memberUpdater.addToSettlement(settlementId, request);
		return MemberResponse.of(member);
	}

	public MemberResponse updatePaymentStatus(Long appointmentMemberId, PaymentStatusUpdateRequest request) {
		Member member = memberUpdater.updatePaymentStatus(appointmentMemberId,
			request);
		List<Member> members = queryMemberService.findAllBySettlementId(
			member.getSettlementId());

		boolean allPaid = members.stream()
			.allMatch(Member::isPaid);

		if (allPaid) {
			member.getSettlement().complete();
		}

		return MemberResponse.of(member);
	}

	public void delete(Long appointmentMemberId) {
		memberDeleter.delete(appointmentMemberId);
	}

}
