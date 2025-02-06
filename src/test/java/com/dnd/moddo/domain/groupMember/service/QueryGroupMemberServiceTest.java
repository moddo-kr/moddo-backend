package com.dnd.moddo.domain.groupMember.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberReader;

@ExtendWith(MockitoExtension.class)
public class QueryGroupMemberServiceTest {

	@Mock
	private GroupMemberReader groupMemberReader;
	@InjectMocks
	private QueryGroupMemberService queryGroupMemberService;

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1),
			"은행", "계좌");
	}

	@DisplayName("모임이 존재하면 모임의 모든 참여자를 조회할 수 있다.")
	@Test
	void findAll() {
		//given
		Long groupId = mockGroup.getId();

		List<GroupMember> mockMembers = List.of(new GroupMember("김반숙", 1, mockGroup));

		when(groupMemberReader.getAllByGroupId(eq(groupId))).thenReturn(mockMembers);
		//when
		GroupMembersResponse response = queryGroupMemberService.findAll(groupId);

		//then
		assertThat(response).isNotNull();
		assertThat(response.members().size()).isEqualTo(1);
		assertThat(response.members().get(0).name()).isEqualTo("김반숙");
		verify(groupMemberReader, times(1)).getAllByGroupId(eq(groupId));
	}
}