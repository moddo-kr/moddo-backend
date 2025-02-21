package com.dnd.moddo.domain.groupMember.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;

class GroupMemberTest {

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now().plusMinutes(1),
			"은행", "계좌", LocalDateTime.now().plusDays(1));
	}

	@DisplayName("참여자의 역할이 총무인 경우 true를 반환한다.")
	@Test
	void testIsManager_whenRoleIsManager() {
		// given
		GroupMember groupMember = GroupMember.builder()
			.name("김모또")
			.group(mockGroup)
			.role(ExpenseRole.MANAGER)
			.isPaid(true)
			.profile("profile.jpg")
			.build();

		// when
		boolean isManager = groupMember.isManager();

		// then
		assertThat(isManager).isTrue();
	}

	@DisplayName("참여자의 역할이 총무가 아닌 경우 false를 반환한다.")
	@Test
	void testIsManager_whenRoleIsNotManager() {
		// given
		GroupMember groupMember = GroupMember.builder()
			.name("김모또")
			.group(mockGroup)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(true)
			.profile("profile.jpg")
			.build();

		// when
		boolean isManager = groupMember.isManager();

		// then
		assertThat(isManager).isFalse();
	}
}
