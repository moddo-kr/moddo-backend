package com.dnd.moddo.domain.appointmentMember.service;

import static org.assertj.core.api.BDDAssertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.appointmentMember.dto.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.domain.appointmentMember.dto.request.appointmentMemberSaveRequest;
import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMemberResponse;
import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.appointmentMember.service.implementation.AppointmentMemberCreator;
import com.dnd.moddo.domain.appointmentMember.service.implementation.AppointmentMemberUpdater;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
public class CommandAppointmentMemberServiceTest {
	@Mock
	private AppointmentMemberCreator appointmentMemberCreator;
	@Mock
	private AppointmentMemberUpdater appointmentMemberUpdater;
	@InjectMocks
	private CommandAppointmentMemberService commandAppointmentMemberService;

	private Settlement mockSettlement;

	@BeforeEach
	void setUp() {
		mockSettlement = GroupTestFactory.createDefault();
	}

	@DisplayName("모든 정보가 유효할때 총무 생성에 성공한다.")
	@Test
	void whenValidInfo_thenCreateSuccess() {
		//given
		Long userId = 1L;
		AppointmentMember expectedMembers = AppointmentMember.builder()
			.name("김모또")
			.settlement(this.mockSettlement)
			.profileId(0)
			.role(ExpenseRole.MANAGER)
			.build();
		Settlement mockSettlement = mock(Settlement.class);
		when(appointmentMemberCreator.createManagerForGroup(any(Settlement.class), eq(userId))).thenReturn(
			expectedMembers);

		// when
		AppointmentMemberResponse response = commandAppointmentMemberService.createManager(mockSettlement, userId);

		//then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("김모또");
		assertThat(response.role()).isEqualTo(ExpenseRole.MANAGER);
		verify(appointmentMemberCreator, times(1)).createManagerForGroup(any(Settlement.class), any());
	}

	@DisplayName("모든 정보가 유효할때 기존 모임의 참여자 추가가 성공한다.")
	@Test
	void whenValidInfo_thenAddGroupMemberSuccess() {
		//given
		Long groupId = mockSettlement.getId();
		appointmentMemberSaveRequest request = mock(appointmentMemberSaveRequest.class);
		AppointmentMember expectedMember = AppointmentMember.builder()
			.name("김반숙")
			.settlement(mockSettlement)
			.profileId(1)
			.role(ExpenseRole.PARTICIPANT)
			.build();

		when(appointmentMemberUpdater.addToGroup(eq(groupId), any(appointmentMemberSaveRequest.class))).thenReturn(
			expectedMember);

		//when
		AppointmentMemberResponse response = commandAppointmentMemberService.addAppointmentMember(groupId, request);

		//then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("김반숙");
		verify(appointmentMemberUpdater, times(1)).addToGroup(eq(groupId), any(appointmentMemberSaveRequest.class));
	}

	@DisplayName("참여자 입금 내역을 업데이트 할 수 있다.")
	@Test
	void whenUpdatePaymentStatus_thenSuccess() {
		//given
		AppointmentMember expectedAppointmentMember = AppointmentMember.builder()
			.name("김반숙")
			.settlement(mockSettlement)
			.isPaid(true)
			.profileId(1)
			.role(ExpenseRole.PARTICIPANT)
			.build();
		PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest(true);
		when(appointmentMemberUpdater.updatePaymentStatus(any(), eq(request))).thenReturn(expectedAppointmentMember);

		//then
		AppointmentMemberResponse response = commandAppointmentMemberService.updatePaymentStatus(1L, request);

		//then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("김반숙");
		assertThat(response.role()).isEqualTo(ExpenseRole.PARTICIPANT);
		assertThat(response.isPaid()).isTrue();

		verify(appointmentMemberUpdater, times(1)).updatePaymentStatus(any(), eq(request));
	}
}
