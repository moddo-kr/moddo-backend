package com.dnd.moddo.domain.groupMember.service.implementation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMemberSaveRequest;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.groupMember.exception.GroupMemberDuplicateNameException;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;

@ExtendWith(MockitoExtension.class)
class GroupMemberUpdaterTest {
	@Mock
	private GroupMemberRepository groupMemberRepository;
	@Mock
	private GroupMemberReader groupMemberReader;
	@Mock
	private GroupMemberValidator groupMemberValidator;
	@Mock
	private GroupRepository groupRepository;
	@InjectMocks
	private GroupMemberUpdater groupMemberUpdater;

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1),
			"은행", "계좌");
	}

	@DisplayName("추가하려는 참여자의 이름이 기존 참여자의 이름과 중복되지 않을경우 참여자 추가에 성공한다.")
	@Test
	void addToGroupSuccess() {
		//given
		Long groupId = mockGroup.getId();
		GroupMemberSaveRequest request = mock(GroupMemberSaveRequest.class);
		when(groupRepository.getById(eq(groupId))).thenReturn(mockGroup);

		List<GroupMember> mockGroupMembers = new ArrayList<>();
		when(groupMemberReader.findAllByGroupId(eq(groupId))).thenReturn(mockGroupMembers);

		doNothing().when(groupMemberValidator).validateMemberNamesNotDuplicate(any());

		GroupMember expectedGroupMember = new GroupMember("김반숙", mockGroup, ExpenseRole.PARTICIPANT);
		when(groupMemberRepository.save(any())).thenReturn(expectedGroupMember);

		//when
		GroupMember result = groupMemberUpdater.addToGroup(groupId, request);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getGroup()).isEqualTo(mockGroup);
		assertThat(result.getName()).isEqualTo("김반숙");

		verify(groupMemberRepository, times(1)).save(any());
	}

	@DisplayName("추가하려는 참여자의 이름이 기존 참여자의 이름과 중복되는 경우 예외가 발생한다..")
	@Test
	void addToGroupDuplicatedName() {
		//given
		Long groupId = mockGroup.getId();
		GroupMemberSaveRequest request = mock(GroupMemberSaveRequest.class);
		when(groupRepository.getById(eq(groupId))).thenReturn(mockGroup);

		List<GroupMember> mockGroupMembers = new ArrayList<>();
		when(groupMemberReader.findAllByGroupId(eq(groupId))).thenReturn(mockGroupMembers);

		doThrow(new GroupMemberDuplicateNameException()).when(groupMemberValidator)
			.validateMemberNamesNotDuplicate(any());

		//when & then
		assertThatThrownBy(() -> {
			groupMemberUpdater.addToGroup(groupId, request);
		}).hasMessage("중복된 참여자의 이름은 저장할 수 없습니다.");
	}
}