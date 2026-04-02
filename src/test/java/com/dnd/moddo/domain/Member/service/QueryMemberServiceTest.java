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

import com.dnd.moddo.common.cache.CacheExecutor;
import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.application.query.QueryMemberService;
import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.member.type.MemberSortType;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.presentation.response.MembersResponse;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
public class QueryMemberServiceTest {

	@Mock
	private MemberReader memberReader;
	@Mock
	private CacheExecutor cacheExecutor;
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

		when(cacheExecutor.execute(anyString(), any(), any())).thenReturn(mockMembers);

		//when
		MembersResponse response = queryMemberService.findAll(groupId, MemberSortType.CREATED);

		//then
		assertThat(response).isNotNull();
		assertThat(response.members().size()).isEqualTo(2);
		assertThat(response.members().get(0).name()).isEqualTo("김모또");
		verify(cacheExecutor, times(1)).execute(anyString(), any(), any());
	}

	@DisplayName("정렬 기준을 받아 모임원을 조회할 수 있다.")
	@Test
	void findAllWithSortType() {
		Long groupId = mockSettlement.getId();

		when(cacheExecutor.execute(anyString(), any(), any())).thenReturn(mockMembers);

		MembersResponse response = queryMemberService.findAll(groupId, MemberSortType.NAME);

		assertThat(response.members()).hasSize(2);
		verify(cacheExecutor).execute(anyString(), any(), any());
	}

	@DisplayName("모임 ID로 원본 Member 목록을 조회할 수 있다.")
	@Test
	void findAllBySettlementId() {
		Long groupId = mockSettlement.getId();

		when(memberReader.findAllBySettlementId(eq(groupId))).thenReturn(mockMembers);

		List<Member> result = queryMemberService.findAllBySettlementId(groupId);

		assertThat(result).isEqualTo(mockMembers);
		verify(memberReader).findAllBySettlementId(groupId);
	}
}
