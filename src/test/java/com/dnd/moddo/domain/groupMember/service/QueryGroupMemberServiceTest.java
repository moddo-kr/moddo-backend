package com.dnd.moddo.domain.groupMember.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberReader;

@ExtendWith(MockitoExtension.class)
public class QueryGroupMemberServiceTest {

	@Mock
	private GroupMemberReader groupMemberReader;
	@InjectMocks
	private QueryGroupMemberService queryGroupMemberService;

	@Test
	public void findAll() {
		//given
		Long meetId = 1L;
		List<GroupMember> mockMembers = List.of(new GroupMember("김반숙", 1, meetId));

		when(groupMemberReader.getAll(eq(meetId))).thenReturn(mockMembers);
		//when
		GroupMembersResponse response = queryGroupMemberService.findAll(meetId);

		//then
		assertThat(response).isNotNull();
		assertThat(response.members().size()).isEqualTo(1);
		assertThat(response.members().get(0).name()).isEqualTo("김반숙");
		verify(groupMemberReader, times(1)).getAll(eq(meetId));
	}
}