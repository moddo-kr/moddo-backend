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

import com.dnd.moddo.event.application.command.CommandMemberService;
import com.dnd.moddo.event.application.impl.MemberCreator;
import com.dnd.moddo.event.application.impl.MemberUpdater;
import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.presentation.request.MemberSaveRequest;
import com.dnd.moddo.event.presentation.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.event.presentation.response.MemberResponse;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
public class CommandMemberServiceTest {
	@Mock
	private MemberCreator memberCreator;
	@Mock
	private MemberUpdater memberUpdater;
	@InjectMocks
	private CommandMemberService commandMemberService;

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
		Member expectedMembers = Member.builder()
			.name("김모또")
			.settlement(this.mockSettlement)
			.profileId(0)
			.role(ExpenseRole.MANAGER)
			.build();
		Settlement mockSettlement = mock(Settlement.class);
		when(memberCreator.createManagerForSettlement(any(Settlement.class), eq(userId))).thenReturn(
			expectedMembers);

		// when
		MemberResponse response = commandMemberService.createManager(mockSettlement, userId);

		//then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("김모또");
		assertThat(response.role()).isEqualTo(ExpenseRole.MANAGER);
		verify(memberCreator, times(1)).createManagerForSettlement(any(Settlement.class), any());
	}

	@DisplayName("모든 정보가 유효할때 기존 모임의 참여자 추가가 성공한다.")
	@Test
	void whenValidInfo_thenAddAppointmentMemberSuccess() {
		//given
		Long groupId = mockSettlement.getId();
		MemberSaveRequest request = mock(MemberSaveRequest.class);
		Member expectedMember = Member.builder()
			.name("김반숙")
			.settlement(mockSettlement)
			.profileId(1)
			.role(ExpenseRole.PARTICIPANT)
			.build();

		when(memberUpdater.addToSettlement(eq(groupId), any(MemberSaveRequest.class))).thenReturn(
			expectedMember);

		//when
		MemberResponse response = commandMemberService.addAppointmentMember(groupId, request);

		//then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("김반숙");
		verify(memberUpdater, times(1)).addToSettlement(eq(groupId),
			any(MemberSaveRequest.class));
	}

	@DisplayName("참여자 입금 내역을 업데이트 할 수 있다.")
	@Test
	void whenUpdatePaymentStatus_thenSuccess() {
		//given
		Member expectedMember = Member.builder()
			.name("김반숙")
			.settlement(mockSettlement)
			.isPaid(true)
			.profileId(1)
			.role(ExpenseRole.PARTICIPANT)
			.build();
		PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest(true);
		when(memberUpdater.updatePaymentStatus(any(), eq(request))).thenReturn(expectedMember);

		//then
		MemberResponse response = commandMemberService.updatePaymentStatus(1L, request);

		//then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("김반숙");
		assertThat(response.role()).isEqualTo(ExpenseRole.PARTICIPANT);
		assertThat(response.isPaid()).isTrue();

		verify(memberUpdater, times(1)).updatePaymentStatus(any(), eq(request));
	}
}
