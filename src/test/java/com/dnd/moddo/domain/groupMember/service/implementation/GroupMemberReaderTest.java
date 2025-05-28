package com.dnd.moddo.domain.groupMember.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.groupMember.exception.GroupMemberNotFoundException;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;
import com.dnd.moddo.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
public class GroupMemberReaderTest {
	@Mock
	private GroupMemberRepository groupMemberRepository;
	@InjectMocks
	private GroupMemberReader groupMemberReader;

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = GroupTestFactory.createDefault();
	}

	@DisplayName("모임이 존재할때 모임의 모든 참여자를 조회에 성공한다.")
	@Test
	void findAllByGroupIdSuccess() {
		//given
		Long groupId = mockGroup.getId();
		List<GroupMember> expectedMembers = List.of(
			GroupMember.builder()
				.name("김모또")
				.group(mockGroup)
				.role(ExpenseRole.MANAGER)
				.isPaid(false)
				.build(),
			GroupMember.builder()
				.name("김반숙")
				.group(mockGroup)
				.role(ExpenseRole.PARTICIPANT)
				.isPaid(false)
				.build()
		);

		when(groupMemberRepository.findByGroupId(eq(groupId))).thenReturn(expectedMembers);

		//when
		List<GroupMember> result = groupMemberReader.findAllByGroupId(groupId);

		//then
		assertThat(result).isNotNull();
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(0).getName()).isEqualTo("김모또");
		assertThat(result.get(0).getRole()).isEqualTo(ExpenseRole.MANAGER);
		verify(groupMemberRepository, times(1)).findByGroupId(groupId);
	}

	@DisplayName("참여자가 존재할때 참여자 id를 사용해 참여자 정보 조회에 성공한다.")
	@Test
	void findByGroupMemberIdSuccess() {
		//given
		Long groupMemberId = 1L;
		GroupMember expectedMember = GroupMember.builder()
			.name("김반숙")
			.group(mockGroup)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();

		when(groupMemberRepository.getById(eq(groupMemberId))).thenReturn(expectedMember);

		//when
		GroupMember result = groupMemberReader.findByGroupMemberId(groupMemberId);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getGroup()).isEqualTo(mockGroup);
		assertThat(result.getName()).isEqualTo("김반숙");
		verify(groupMemberRepository, times(1)).getById(eq(groupMemberId));

	}

	@DisplayName("참여자가 존재하지 않을때 참여자 id를 사용해 조회하려고 하면 예외가 발생한다.")
	@Test
	void findByGroupMemberIdFail() {
		//given
		Long groupMemberId = 1L;
		doThrow(new GroupMemberNotFoundException(groupMemberId)).when(groupMemberRepository).getById(eq(groupMemberId));

		//when & then
		assertThatThrownBy(() -> {
			groupMemberReader.findByGroupMemberId(groupMemberId);
		}).hasMessage("해당 참여자를 찾을 수 없습니다. (GroupMember ID: " + groupMemberId + ")");
	}

}
