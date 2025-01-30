package com.dnd.moddo.domain.groupMember.entity;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
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
		assertEquals(2, groupMembers.size());  // meetId가 1인 멤버가 2명 있는지 확인
		assertTrue(groupMembers.stream().anyMatch(member -> "김완숙".equals(member.getName())));
		assertTrue(groupMembers.stream().anyMatch(member -> "정에그".equals(member.getName())));
	}

}