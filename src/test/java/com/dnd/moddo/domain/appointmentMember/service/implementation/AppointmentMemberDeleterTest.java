package com.dnd.moddo.domain.appointmentMember.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.appointmentMember.exception.AppointmentMemberNotFoundException;
import com.dnd.moddo.domain.appointmentMember.repository.AppointmentMemberRepository;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
class AppointmentMemberDeleterTest {

	@Mock
	private AppointmentMemberRepository appointmentMemberRepository;
	@Mock
	private AppointmentMemberReader appointmentMemberReader;
	@InjectMocks
	private AppointmentMemberDeleter appointmentMemberDeleter;

	private Settlement mockSettlement;

	@BeforeEach
	void setUp() {
		mockSettlement = GroupTestFactory.createDefault();
	}

	@DisplayName("유효한 참여자 id로 삭제를 요청하면 성공적으로 삭제된다.")
	@Test
	void delete_Success_ValidGroupMemberId() {
		//given
		Long groupMemberId = 1L;
		AppointmentMember expectedMember = AppointmentMember.builder()
			.name("김반숙")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();

		when(appointmentMemberReader.findByAppointmentMemberId(eq(groupMemberId))).thenReturn(expectedMember);
		doNothing().when(appointmentMemberRepository).delete(any(AppointmentMember.class));

		//when
		appointmentMemberDeleter.delete(groupMemberId);

		//then
		verify(appointmentMemberRepository, times(1)).delete(any(AppointmentMember.class));
	}

	@DisplayName("유효하지 않은 참여자 id로 삭제를 요청하면 예외가 발생한다.")
	@Test
	void delete_ThrowException_WithInvalidExpenseId() {
		//given
		Long appointmentMember = 1L;

		doThrow(new AppointmentMemberNotFoundException(appointmentMember)).when(appointmentMemberReader)
			.findByAppointmentMemberId(eq(appointmentMember));

		//when & then
		assertThatThrownBy(() -> {
			appointmentMemberDeleter.delete(appointmentMember);
		}).hasMessage("해당 참여자를 찾을 수 없습니다. (AppointmentMember ID: " + appointmentMember + ")");
	}

	@DisplayName("유효한 참여자 id로 삭제를 요청하면 성공적으로 삭제된다.")
	@Test
	void delete_ThrowException_WhenRoleIsManager() {
		//given
		Long appointmentMember = 1L;
		AppointmentMember expectedMember = AppointmentMember.builder()
			.name("김모또")
			.settlement(mockSettlement)
			.role(ExpenseRole.MANAGER)
			.isPaid(false)
			.build();

		when(appointmentMemberReader.findByAppointmentMemberId(eq(appointmentMember))).thenReturn(expectedMember);

		//when & then
		assertThatThrownBy(() -> {
			appointmentMemberDeleter.delete(appointmentMember);
		}).hasMessage("총무(MANAGER)는 삭제할 수 없습니다. (Member ID: " + appointmentMember + ")");
	}
}
