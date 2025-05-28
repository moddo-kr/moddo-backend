package com.dnd.moddo.domain.groupMember.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.support.GroupTestFactory;

class GroupMemberTest {

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = GroupTestFactory.createDefault();
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
			.build();

		// when
		boolean isManager = groupMember.isManager();

		// then
		assertThat(isManager).isFalse();
	}

	@DisplayName("참여자가 입금 상태를 변경할 수 있다.")
	@Test
	void testUpdatePaymentStatus() {
		// given
		GroupMember groupMember = GroupMember.builder()
			.name("김모또")
			.group(mockGroup)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();

		// when
		groupMember.updatePaymentStatus(true);

		// then
		assertThat(groupMember.isPaid()).isTrue();
		assertThat(groupMember.getPaidAt()).isNotNull();
	}
}
