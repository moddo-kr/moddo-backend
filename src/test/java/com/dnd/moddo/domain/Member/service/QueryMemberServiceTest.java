package com.dnd.moddo.domain.Member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.application.query.QueryMemberService;
import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.presentation.response.MembersResponse;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
public class QueryMemberServiceTest {

	@Mock
	private MemberReader memberReader;
	@InjectMocks
	private QueryMemberService queryMemberService;

	private Settlement mockSettlement;
	private List<Member> mockMembers;

	@BeforeEach
	void setUp() {
		mockSettlement = GroupTestFactory.createDefault();

		mockMembers = List.of(
			Member.builder()
				.name("김모또")
				.settlement(mockSettlement)
				.profileId(0)
				.role(ExpenseRole.MANAGER)
				.build(),
			Member.builder()
				.name("김반숙")
				.profileId(1)
				.settlement(mockSettlement)
				.role(ExpenseRole.PARTICIPANT)
				.build()
		);
	}

	@DisplayName("모임이 존재하면 모임의 모든 참여자를 조회할 수 있다.")
	@Test
	void findAll() {
		//given
		Long groupId = mockSettlement.getId();

		when(memberReader.findAllBySettlementId(eq(groupId))).thenReturn(mockMembers);

		//when
		MembersResponse response = queryMemberService.findAll(groupId);

		//then
		assertThat(response).isNotNull();
		assertThat(response.members().size()).isEqualTo(2);
		assertThat(response.members().get(0).name()).isEqualTo("김모또");
		verify(memberReader, times(1)).findAllBySettlementId(eq(groupId));
	}
}
