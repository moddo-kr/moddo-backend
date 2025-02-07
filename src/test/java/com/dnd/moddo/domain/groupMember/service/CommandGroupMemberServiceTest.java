package com.dnd.moddo.domain.groupMember.service;

import static org.assertj.core.api.BDDAssertions.*;
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
import com.dnd.moddo.domain.groupMember.dto.request.GroupMemberSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMembersSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMemberResponse;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberCreator;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberUpdater;

@ExtendWith(MockitoExtension.class)
public class CommandGroupMemberServiceTest {
	@Mock
	private GroupMemberCreator groupMemberCreator;
	@Mock
	private GroupMemberUpdater groupMemberUpdater;
	@InjectMocks
	private CommandGroupMemberService commandGroupMemberService;

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1),
			"은행", "계좌");
	}

	@DisplayName("모든 정보가 유효할때 모임에 참여자 추가가 성공한다.")
	@Test
	void createSuccess() {
		//given
		Long groupId = mockGroup.getId();
		GroupMembersSaveRequest request = new GroupMembersSaveRequest(new ArrayList<>());
		List<GroupMember> expectedMembers = List.of(new GroupMember("김반숙", 1, mockGroup));

		when(groupMemberCreator.create(eq(groupId), eq(request))).thenReturn(expectedMembers);

		// when
		GroupMembersResponse response = commandGroupMemberService.create(groupId, request);

		//then
		assertThat(response).isNotNull();
		assertThat(response.members().size()).isEqualTo(1);
		assertThat(response.members().get(0).name()).isEqualTo("김반숙");
		verify(groupMemberCreator, times(1)).create(eq(groupId), eq(request));
	}

	@DisplayName("모든 정보가 유효할때 기존 모임의 참여자 추가가 성공한다.")
	@Test
	void addGroupMemberSuccess() {
		//given
		Long groupId = mockGroup.getId();
		GroupMemberSaveRequest request = mock(GroupMemberSaveRequest.class);
		GroupMember expectedMember = new GroupMember("김반숙", mockGroup);

		when(groupMemberUpdater.addToGroup(eq(groupId), any(GroupMemberSaveRequest.class))).thenReturn(expectedMember);

		//when
		GroupMemberResponse result = commandGroupMemberService.addGroupMember(groupId, request);

		//then
		assertThat(result).isNotNull();
		assertThat(result.name()).isEqualTo("김반숙");
		verify(groupMemberUpdater, times(1)).addToGroup(eq(groupId), any(GroupMemberSaveRequest.class));
	}
}