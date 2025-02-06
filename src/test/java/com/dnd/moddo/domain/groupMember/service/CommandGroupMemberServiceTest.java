package com.dnd.moddo.domain.groupMember.service;

import static org.assertj.core.api.BDDAssertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMembersSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberCreator;

@ExtendWith(MockitoExtension.class)
public class CommandGroupMemberServiceTest {
	@Mock
	private GroupMemberCreator groupMemberCreator;
	@InjectMocks
	private CommandGroupMemberService commandGroupMemberService;

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1),
			"은행", "계좌");
	}

	@Test
	void createGroupMembersSuccess() {
		//given
		Long groupId = mockGroup.getId();
		GroupMembersSaveRequest request = new GroupMembersSaveRequest(new ArrayList<>());
		List<GroupMember> mockMembers = List.of(new GroupMember("김반숙", 1, mockGroup));

		when(groupMemberCreator.createGroupMember(eq(groupId), eq(request))).thenReturn(mockMembers);

		// when
		GroupMembersResponse response = commandGroupMemberService.createGroupMembers(groupId, request);

		//then
		assertThat(response).isNotNull();
		assertThat(response.members().size()).isEqualTo(1);
		assertThat(response.members().get(0).name()).isEqualTo("김반숙");
		verify(groupMemberCreator, times(1)).createGroupMember(eq(groupId), eq(request));
	}
}