package com.dnd.moddo.domain.Member.service;

import static org.assertj.core.api.BDDAssertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

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
import com.dnd.moddo.event.application.query.QueryMemberService;
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
	@Mock
	private QueryMemberService queryMemberService;
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

	@DisplayName("모든 참여자가 입금 완료되면 정산이 완료된다")
	@Test
	void whenAllMembersPaid_thenSettlementCompleted() {
		// given
		Settlement mockSettlement = mock(Settlement.class);

		Member paidMember = Member.builder()
			.name("김반숙")
			.settlement(mockSettlement)
			.isPaid(true)
			.profileId(1)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(true)
			.build();

		PaymentStatusUpdateRequest request =
			new PaymentStatusUpdateRequest(true);

		when(memberUpdater.updatePaymentStatus(any(), eq(request)))
			.thenReturn(paidMember);

		when(queryMemberService.findAllBySettlementId(any()))
			.thenReturn(List.of(paidMember));

		// when
		MemberResponse response =
			commandMemberService.updatePaymentStatus(1L, request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("김반숙");
		assertThat(response.isPaid()).isTrue();

		verify(memberUpdater).updatePaymentStatus(any(), eq(request));
		verify(queryMemberService).findAllBySettlementId(any());

		verify(mockSettlement).complete();
	}

}
