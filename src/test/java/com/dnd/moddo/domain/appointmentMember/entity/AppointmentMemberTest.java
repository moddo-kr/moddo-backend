package com.dnd.moddo.domain.appointmentMember.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.global.support.GroupTestFactory;

class AppointmentMemberTest {

	private Settlement mockSettlement;

	@BeforeEach
	void setUp() {
		mockSettlement = GroupTestFactory.createDefault();
	}

	@DisplayName("참여자의 역할이 총무인 경우 true를 반환한다.")
	@Test
	void testIsManager_whenRoleIsManager() {
		// given
		AppointmentMember appointmentMember = AppointmentMember.builder()
			.name("김모또")
			.settlement(mockSettlement)
			.role(ExpenseRole.MANAGER)
			.isPaid(true)
			.build();

		// when
		boolean isManager = appointmentMember.isManager();

		// then
		assertThat(isManager).isTrue();
	}

	@DisplayName("참여자의 역할이 총무가 아닌 경우 false를 반환한다.")
	@Test
	void testIsManager_whenRoleIsNotManager() {
		// given
		AppointmentMember appointmentMember = AppointmentMember.builder()
			.name("김모또")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(true)
			.build();

		// when
		boolean isManager = appointmentMember.isManager();

		// then
		assertThat(isManager).isFalse();
	}

	@DisplayName("참여자가 입금 상태를 변경할 수 있다.")
	@Test
	void testUpdatePaymentStatus() {
		// given
		AppointmentMember appointmentMember = AppointmentMember.builder()
			.name("김모또")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();

		// when
		appointmentMember.updatePaymentStatus(true);

		// then
		assertThat(appointmentMember.isPaid()).isTrue();
		assertThat(appointmentMember.getPaidAt()).isNotNull();
	}
}
