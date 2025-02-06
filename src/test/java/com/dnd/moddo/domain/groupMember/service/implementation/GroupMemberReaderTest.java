package com.dnd.moddo.domain.groupMember.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;

@ExtendWith(MockitoExtension.class)
public class GroupMemberReaderTest {
	@Mock
	private GroupMemberRepository groupMemberRepository;
	@InjectMocks
	private GroupMemberReader groupMemberReader;

	@Test
	public void getAll() {
		//given
		Long groupId = 1L;
		List<GroupMember> expectedMembers = List.of(new GroupMember("김반숙", 1, groupId));

		when(groupMemberRepository.findByGroupId(groupId)).thenReturn(expectedMembers);

		//when
		List<GroupMember> groupMembers = groupMemberReader.getAllByGroupId(groupId);

		//then
		assertThat(groupMembers).isNotNull();
		assertThat(groupMembers.size()).isEqualTo(1);
		assertThat(groupMembers.get(0).getName()).isEqualTo("김반숙");
		verify(groupMemberRepository, times(1)).findByGroupId(groupId);
	}

}