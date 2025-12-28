package com.dnd.moddo.event.application.query;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.presentation.response.MembersResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QueryMemberService {
	private final MemberReader memberReader;

	public MembersResponse findAll(Long settlementId) {
		List<Member> members = memberReader.findAllBySettlementId(settlementId);
		return MembersResponse.of(members);
	}

	public List<Member> findAllBySettlementId(Long settlementId) {
		return memberReader.findAllBySettlementId(settlementId);
	}
}
