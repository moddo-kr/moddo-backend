package com.dnd.moddo.domain.Member.entity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.global.support.GroupTestFactory;
import com.dnd.moddo.user.domain.User;

class MemberTest {

	private Settlement mockSettlement;

	@BeforeEach
	void setUp() {
		mockSettlement = GroupTestFactory.createDefault();
	}

	@DisplayName("참여자의 역할이 총무인 경우 true를 반환한다.")
	@Test
	void testIsManager_whenRoleIsManager() {
		// given
		Member member = Member.builder()
			.name("김모또")
			.settlement(mockSettlement)
			.role(ExpenseRole.MANAGER)
			.isPaid(true)
			.build();

		// when
		boolean isManager = member.isManager();

		// then
		assertThat(isManager).isTrue();
	}

	@DisplayName("참여자의 역할이 총무가 아닌 경우 false를 반환한다.")
	@Test
	void testIsManager_whenRoleIsNotManager() {
		// given
		Member member = Member.builder()
			.name("김모또")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(true)
			.build();

		// when
		boolean isManager = member.isManager();

		// then
		assertThat(isManager).isFalse();
	}

	@DisplayName("참여자가 입금 상태를 변경할 수 있다.")
	@Test
	void testUpdatePaymentStatus() {
		// given
		Member member = Member.builder()
			.name("김모또")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();

		// when
		member.updatePaymentStatus(true);

		// then
		assertThat(member.isPaid()).isTrue();
		assertThat(member.getPaidAt()).isNotNull();
	}

	@DisplayName("사용자를 정상적으로 할당할 수 있다.")
	@Test
	void assignUser_success() {
		// given
		Member member = Member.builder()
			.name("기존이름")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();

		User user = mock(User.class);
		when(user.getName()).thenReturn("새이름");

		// when
		member.assignUser(user);

		// then
		assertThat(member.getUser()).isEqualTo(user);
		assertThat(member.getName()).isEqualTo("새이름"); // 동기화 확인
	}

	@DisplayName("이미 사용자가 연결되어 있으면 예외가 발생한다.")
	@Test
	void assignUser_throwException_whenUserAlreadyAssigned() {
		// given
		Member member = Member.builder()
			.name("기존이름")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();

		User firstUser = mock(User.class);
		User secondUser = mock(User.class);

		when(firstUser.getName()).thenReturn("첫번째");
		when(secondUser.getName()).thenReturn("두번째");

		member.assignUser(firstUser);

		// when & then
		assertThatThrownBy(() -> member.assignUser(secondUser))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("이미 사용자와 연결된 멤버입니다.");
	}

	@DisplayName("null 사용자를 할당하면 예외가 발생한다.")
	@Test
	void assignUser_throwException_whenUserIsNull() {
		Member member = Member.builder()
			.name("기존이름")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();

		assertThatThrownBy(() -> member.assignUser(null))
			.isInstanceOf(NullPointerException.class);
	}
}
