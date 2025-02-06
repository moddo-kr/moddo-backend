package com.dnd.moddo.domain.groupMember.service.implementation;

import static org.assertj.core.api.BDDAssertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.groupMember.dto.request.GroupMembersSaveRequest;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;

@ExtendWith(MockitoExtension.class)
public class GroupMemberCreatorTest {
	@Mock
	private GroupMemberRepository groupMemberRepository;

	@InjectMocks
	private GroupMemberCreator groupMemberCreator;

	@Test
	public void createGroupMember() {
		//given
		Long groupId = 1L;
		GroupMembersSaveRequest request = new GroupMembersSaveRequest(new ArrayList<>());

		List<GroupMember> expectedMembers = List.of(new GroupMember("김반숙", 1, groupId));

		when(groupMemberRepository.saveAll(anyList())).thenReturn(expectedMembers);

		//when
		List<GroupMember> savedMembers = groupMemberCreator.createGroupMember(groupId, request);

		//then
		assertThat(savedMembers).isNotNull();
		assertThat(savedMembers.size()).isEqualTo(1);
		assertThat(savedMembers.get(0).getName()).isEqualTo("김반숙");
		verify(groupMemberRepository, times(1)).saveAll(anyList());

	}
}