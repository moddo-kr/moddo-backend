package com.dnd.moddo.domain.groupMember.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

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
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
class GroupMemberDeleterTest {

	@Mock
	private GroupMemberRepository groupMemberRepository;
	@Mock
	private GroupMemberReader groupMemberReader;
	@InjectMocks
	private GroupMemberDeleter groupMemberDeleter;

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = GroupTestFactory.createDefault();
	}

	@DisplayName("유효한 참여자 id로 삭제를 요청하면 성공적으로 삭제된다.")
	@Test
	void delete_Success_ValidGroupMemberId() {
		//given
		Long groupMemberId = 1L;
		GroupMember expectedMember = GroupMember.builder()
			.name("김반숙")
			.group(mockGroup)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();

		when(groupMemberReader.findByGroupMemberId(eq(groupMemberId))).thenReturn(expectedMember);
		doNothing().when(groupMemberRepository).delete(any(GroupMember.class));

		//when
		groupMemberDeleter.delete(groupMemberId);

		//then
		verify(groupMemberRepository, times(1)).delete(any(GroupMember.class));
	}

	@DisplayName("유효하지 않은 참여자 id로 삭제를 요청하면 예외가 발생한다.")
	@Test
	void delete_ThrowException_WithInvalidExpenseId() {
		//given
		Long groupMemberId = 1L;

		doThrow(new GroupMemberNotFoundException(groupMemberId)).when(groupMemberReader)
			.findByGroupMemberId(eq(groupMemberId));

		//when & then
		assertThatThrownBy(() -> {
			groupMemberDeleter.delete(groupMemberId);
		}).hasMessage("해당 참여자를 찾을 수 없습니다. (GroupMember ID: " + groupMemberId + ")");
	}

	@DisplayName("유효한 참여자 id로 삭제를 요청하면 성공적으로 삭제된다.")
	@Test
	void delete_ThrowException_WhenRoleIsManager() {
		//given
		Long groupMemberId = 1L;
		GroupMember expectedMember = GroupMember.builder()
			.name("김모또")
			.group(mockGroup)
			.role(ExpenseRole.MANAGER)
			.isPaid(false)
			.build();

		when(groupMemberReader.findByGroupMemberId(eq(groupMemberId))).thenReturn(expectedMember);

		//when & then
		assertThatThrownBy(() -> {
			groupMemberDeleter.delete(groupMemberId);
		}).hasMessage("총무(MANAGER)는 삭제할 수 없습니다. (Member ID: " + groupMemberId + ")");
	}
}
