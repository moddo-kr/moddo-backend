package com.dnd.moddo.event.application.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.infrastructure.MemberRepository;
import com.dnd.moddo.user.domain.User;
import com.dnd.moddo.user.infrastructure.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberCreator {
	private final MemberRepository memberRepository;
	private final UserRepository userRepository;

	public Member createManagerForSettlement(Settlement settlement, Long userId) {
		User user = userRepository.getById(userId);

		String name = user.getIsMember() ? user.getName() : "김모또";

		Member member = Member.builder()
			.name(name)
			.settlement(settlement)
			.profileId(null)
			.profileId(0)
			.role(ExpenseRole.MANAGER)
			.build();

		member.updatePaymentStatus(true);

		return memberRepository.save(member);
	}
}
