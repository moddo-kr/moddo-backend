package com.dnd.moddo.domain.groupMember.entity;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class GroupMemberTest {

	@Autowired
	private GroupMemberRepository groupMemberRepository;

	@DisplayName("모임로 참여자를 조회할 수 있다.")
	@Test
	public void findByMeetId() {
		// Given
		GroupMember groupMember1 = new GroupMember("김완숙", 1, 1L);
		GroupMember groupMember2 = new GroupMember("정에그", 2, 1L);
		groupMemberRepository.save(groupMember1);
		groupMemberRepository.save(groupMember2);

		// When
		List<GroupMember> groupMembers = groupMemberRepository.findByMeetId(1L);

		// Then
		assertThat(groupMembers.size()).isEqualTo(2);  // meetId가 1인 멤버가 2명 있는지 확인
		assertThat(groupMembers.stream().anyMatch(member -> "김완숙".equals(member.getName()))).isTrue();
		assertThat(groupMembers.stream().anyMatch(member -> "정에그".equals(member.getName()))).isTrue();
	}

}