package com.dnd.moddo.domain.Member.entity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.global.support.GroupTestFactory;
import com.dnd.moddo.global.support.UserTestFactory;
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

		// when
		member.assignUser(user);

		// then
		assertThat(member.getUser()).isEqualTo(user);
		assertThat(member.getName()).isEqualTo("기존이름");
	}

	@DisplayName("사용자가 연결된 참여자는 선택된 상태이다.")
	@Test
	void isAssigned_whenUserAssigned_returnsTrue() {
		Member member = Member.builder()
			.name("기존이름")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();

		member.assignUser(mock(User.class));

		assertThat(member.isAssigned()).isTrue();
	}

	@DisplayName("사용자가 연결되지 않은 참여자는 선택되지 않은 상태이다.")
	@Test
	void isAssigned_whenUserNotAssigned_returnsFalse() {
		Member member = Member.builder()
			.name("기존이름")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();

		assertThat(member.isAssigned()).isFalse();
	}

	@DisplayName("본인이 선택한 참여자인지 확인할 수 있다.")
	@Test
	void isAssignedTo_whenSameUser_returnsTrue() throws Exception {
		Member member = Member.builder()
			.name("기존이름")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();

		User user = UserTestFactory.createWithEmail("assigned@test.com");
		setId(user, 1L);
		member.assignUser(user);

		assertThat(member.isAssignedTo(1L)).isTrue();
	}

	@DisplayName("다른 사용자가 선택한 참여자인지 확인하면 false를 반환한다.")
	@Test
	void isAssignedTo_whenDifferentUser_returnsFalse() throws Exception {
		Member member = Member.builder()
			.name("기존이름")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();

		User user = UserTestFactory.createWithEmail("assigned2@test.com");
		setId(user, 1L);
		member.assignUser(user);

		assertThat(member.isAssignedTo(2L)).isFalse();
	}

	@DisplayName("정산에 속한 참여자인지 확인할 수 있다.")
	@Test
	void isInSettlement_whenSameSettlement_returnsTrue() throws Exception {
		Member member = Member.builder()
			.name("기존이름")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();
		setSettlementId(mockSettlement, 1L);

		assertThat(member.isInSettlement(1L)).isTrue();
	}

	@DisplayName("다른 정산의 참여자인지 확인하면 false를 반환한다.")
	@Test
	void isInSettlement_whenDifferentSettlement_returnsFalse() throws Exception {
		Member member = Member.builder()
			.name("기존이름")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();
		setSettlementId(mockSettlement, 1L);

		assertThat(member.isInSettlement(2L)).isFalse();
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

	@DisplayName("본인이 연결한 사용자를 정상적으로 해제할 수 있다.")
	@Test
	void unassignUser_success() throws Exception {
		Member member = Member.builder()
			.name("기존이름")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();

		User user = UserTestFactory.createWithEmail("test1@test.com");
		setId(user, 1L);
		member.assignUser(user);

		member.unassignUser(1L);

		assertThat(member.getUser()).isNull();
	}

	@DisplayName("다른 사용자가 선택한 참여자는 해제할 수 없다.")
	@Test
	void unassignUser_throwException_whenDifferentUser() throws Exception {
		Member member = Member.builder()
			.name("기존이름")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();

		User user = UserTestFactory.createWithEmail("test2@test.com");
		setId(user, 1L);
		member.assignUser(user);

		assertThatThrownBy(() -> member.unassignUser(2L))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("본인이 선택한 참여자만 해제할 수 있습니다.");
	}

	private void setId(User user, Long id) throws Exception {
		Field idField = User.class.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(user, id);
	}

	private void setSettlementId(Settlement settlement, Long id) throws Exception {
		Field idField = Settlement.class.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(settlement, id);
	}
}
