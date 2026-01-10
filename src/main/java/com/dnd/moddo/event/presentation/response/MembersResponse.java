package com.dnd.moddo.event.presentation.response;

import java.util.List;

import com.dnd.moddo.event.domain.member.Member;

public record MembersResponse(List<MemberResponse> members) {
	public static MembersResponse of(List<Member> members) {
		return new MembersResponse(members.stream()
			.map(MemberResponse::of)
			.toList());
	}
}
