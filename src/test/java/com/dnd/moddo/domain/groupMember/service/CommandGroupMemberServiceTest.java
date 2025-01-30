package com.dnd.moddo.domain.groupMember.service;

import static org.assertj.core.api.BDDAssertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.groupMember.dto.request.GroupMembersSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberCreator;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberUpdator;

@ExtendWith(MockitoExtension.class)
public class CommandGroupMemberServiceTest {
	@Mock
	private GroupMemberCreator groupMemberCreator;
	@Mock
	private GroupMemberUpdator groupMemberUpdator;
	@InjectMocks
	private CommandGroupMemberService commandGroupMemberService;

	@Test
	public void createGroupMembers() {
		//given
		Long meetId = 1L;
		GroupMembersSaveRequest request = new GroupMembersSaveRequest(new ArrayList<>());
		List<GroupMember> mockMembers = List.of(new GroupMember("김반숙", 1, meetId));

		when(groupMemberCreator.createGroupMember(eq(meetId), eq(request))).thenReturn(mockMembers);

		// when
		GroupMembersResponse response = commandGroupMemberService.createGroupMembers(meetId, request);

		//then
		assertThat(response).isNotNull();
		assertThat(response.members().size()).isEqualTo(1);
		assertThat(response.members().get(0).name()).isEqualTo("김반숙");
		verify(groupMemberCreator, times(1)).createGroupMember(eq(meetId), eq(request));
	}
}