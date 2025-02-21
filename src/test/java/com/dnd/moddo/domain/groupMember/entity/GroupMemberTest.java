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

	@DisplayName("참여자가 입금 상태를 변경할 수 있다.")
	@Test
	void testUpdatePaymentStatus() {
		// given
		GroupMember groupMember = GroupMember.builder()
			.name("김모또")
			.group(mockGroup)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.profile("profile.jpg")
			.build();

		// when
		groupMember.updatePaymentStatus(true);

		// then
		assertThat(groupMember.isPaid()).isTrue();
		assertThat(groupMember.getPaidAt()).isNotNull();
	}

	@DisplayName("참여자의 프로필을 업데이트할 수 있다.")
	@Test
	void testUpdateProfile() {
		// given
		GroupMember groupMember = GroupMember.builder()
			.name("김모또")
			.group(mockGroup)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(true)
			.profile("profile.jpg")
			.build();

		String newProfile = "newProfile.jpg";

		// when
		groupMember.updateProfile(newProfile);

		// then
		assertThat(groupMember.getProfile()).isEqualTo(newProfile);
	}
}
