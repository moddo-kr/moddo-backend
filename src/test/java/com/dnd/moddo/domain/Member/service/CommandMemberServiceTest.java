package com.dnd.moddo.domain.Member.service;

import static org.assertj.core.api.BDDAssertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.common.cache.CacheEvictor;
import com.dnd.moddo.event.application.command.CommandMemberService;
import com.dnd.moddo.event.application.impl.MemberCreator;
import com.dnd.moddo.event.application.impl.MemberDeleter;
import com.dnd.moddo.event.application.impl.MemberUpdater;
import com.dnd.moddo.event.application.impl.SettlementCompletionProcessor;
import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.presentation.request.MemberSaveRequest;
import com.dnd.moddo.event.presentation.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.event.presentation.response.MemberResponse;
import com.dnd.moddo.global.support.GroupTestFactory;
import com.dnd.moddo.global.support.UserTestFactory;
import com.dnd.moddo.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class CommandMemberServiceTest {
	@Mock
	private MemberCreator memberCreator;
	@Mock
	private MemberUpdater memberUpdater;
	@Mock
	private MemberDeleter memberDeleter;
	@Mock
	private SettlementCompletionProcessor settlementCompletionProcessor;
	@Mock
	private CacheEvictor cacheEvictor;
	@InjectMocks
	private CommandMemberService commandMemberService;

	private Settlement mockSettlement;

	@BeforeEach
	void setUp() {
		mockSettlement = GroupTestFactory.createDefault();
		setField(mockSettlement, 1L);
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
		MemberResponse response = commandMemberService.addMember(groupId, request);

		//then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("김반숙");
		verify(memberUpdater, times(1)).addToSettlement(eq(groupId),
			any(MemberSaveRequest.class));
		verify(cacheEvictor).evictMembers(groupId);
		verify(cacheEvictor).evictSettlementListsBySettlement(groupId);
	}

	@DisplayName("모든 참여자가 입금 완료되면 정산이 완료된다")
	@Test
	void whenAllMembersPaid_thenSettlementCompleted() {
		// given
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

		// when
		MemberResponse response =
			commandMemberService.updatePaymentStatus(1L, request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("김반숙");
		assertThat(response.isPaid()).isTrue();

		verify(memberUpdater).updatePaymentStatus(any(), eq(request));
		verify(settlementCompletionProcessor).completeIfAllPaid(mockSettlement.getId());
		verify(cacheEvictor).evictMembers(mockSettlement.getId());
		verify(cacheEvictor).evictSettlementListsBySettlement(mockSettlement.getId());
	}

	@DisplayName("로그인 사용자가 참여자를 선택할 수 있다.")
	@Test
	void assignMemberSuccess() {
		Long settlementId = 1L;
		Long memberId = 2L;
		Long userId = 3L;

		User user = UserTestFactory.createWithEmail("assign@test.com");
		setId(user, userId);
		Member member = Member.builder()
			.name("김반숙")
			.settlement(mockSettlement)
			.profileId(1)
			.role(ExpenseRole.PARTICIPANT)
			.user(user)
			.build();

		when(memberUpdater.assignMember(settlementId, memberId, userId)).thenReturn(member);

		MemberResponse response = commandMemberService.assignMember(settlementId, memberId, userId);

		assertThat(response.name()).isEqualTo("김반숙");
		assertThat(response.userId()).isEqualTo(member.getUserId());
		verify(memberUpdater).assignMember(settlementId, memberId, userId);
		verify(cacheEvictor).evictMembers(settlementId);
		verify(cacheEvictor).evictSettlementListsBySettlement(settlementId, userId);
	}

	@DisplayName("로그인 사용자가 자신이 선택한 참여자를 해제할 수 있다.")
	@Test
	void unassignMemberSuccess() {
		Long settlementId = 1L;
		Long memberId = 2L;
		Long userId = 3L;

		Member member = Member.builder()
			.name("김반숙")
			.settlement(mockSettlement)
			.profileId(1)
			.role(ExpenseRole.PARTICIPANT)
			.build();

		when(memberUpdater.unassignMember(settlementId, memberId, userId)).thenReturn(member);

		MemberResponse response = commandMemberService.unassignMember(settlementId, memberId, userId);

		assertThat(response.name()).isEqualTo("김반숙");
		assertThat(response.userId()).isNull();
		verify(memberUpdater).unassignMember(settlementId, memberId, userId);
		verify(cacheEvictor).evictMembers(settlementId);
		verify(cacheEvictor).evictSettlementListsBySettlement(settlementId, userId);
	}

	@DisplayName("참여자를 삭제하면 관련 캐시를 비운다.")
	@Test
	void deleteMemberEvictsCaches() {
		Long settlementId = 1L;
		Long userId = 3L;
		Member deletedMember = Member.builder()
			.name("김반숙")
			.settlement(mockSettlement)
			.profileId(1)
			.role(ExpenseRole.PARTICIPANT)
			.user(UserTestFactory.createWithEmail("delete@test.com"))
			.build();
		setId(deletedMember.getUser(), userId);

		when(memberDeleter.delete(2L)).thenReturn(deletedMember);

		commandMemberService.delete(2L);

		verify(memberDeleter).delete(2L);
		verify(cacheEvictor).evictMembers(settlementId);
		verify(cacheEvictor).evictSettlementListsBySettlement(settlementId, userId);
	}

	private void setId(User user, Long id) {
		try {
			Field idField = User.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(user, id);
		} catch (ReflectiveOperationException exception) {
			throw new RuntimeException(exception);
		}
	}

	private void setField(Settlement settlement, Long id) {
		try {
			Field idField = Settlement.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(settlement, id);
		} catch (ReflectiveOperationException exception) {
			throw new RuntimeException(exception);
		}
	}

}
