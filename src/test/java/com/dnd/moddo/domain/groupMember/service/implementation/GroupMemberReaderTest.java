package com.dnd.moddo.domain.groupMember.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;

@ExtendWith(MockitoExtension.class)
public class GroupMemberReaderTest {
	@Mock
	private GroupMemberRepository groupMemberRepository;
	@InjectMocks
	private GroupMemberReader groupMemberReader;

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1),
			"은행", "계좌");
	}

	@Test
	void getAll() {
		//given
		Long groupId = mockGroup.getId();
		List<GroupMember> expectedMembers = List.of(new GroupMember("김반숙", 1, mockGroup));

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