package com.dnd.moddo.domain.groupMember.service;

import static org.assertj.core.api.BDDAssertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMemberSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMemberResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberCreator;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberUpdater;

@ExtendWith(MockitoExtension.class)
public class CommandGroupMemberServiceTest {
	@Mock
	private GroupMemberCreator groupMemberCreator;
	@Mock
	private GroupMemberUpdater groupMemberUpdater;
	@InjectMocks
	private CommandGroupMemberService commandGroupMemberService;

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now().plusMinutes(1),
			"은행", "계좌", LocalDateTime.now().plusDays(1));
	}

	@DisplayName("모든 정보가 유효할때 총무 생성에 성공한다.")
	@Test
	void createSuccess() {
		//given
		Long userId = 1L;
		GroupMember expectedMembers = GroupMember.builder()
			.name("김모또")
			.group(mockGroup)
			.profileId(0)
			.role(ExpenseRole.MANAGER)
			.build();
		Group mockGroup = mock(Group.class);
		when(groupMemberCreator.createManagerForGroup(any(Group.class), eq(userId))).thenReturn(expectedMembers);

		// when
		GroupMemberResponse response = commandGroupMemberService.createManager(mockGroup, userId);

		//then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("김모또");
		assertThat(response.role()).isEqualTo(ExpenseRole.MANAGER);
		verify(groupMemberCreator, times(1)).createManagerForGroup(any(Group.class), any());
	}

	@DisplayName("모든 정보가 유효할때 기존 모임의 참여자 추가가 성공한다.")
	@Test
	void addGroupMemberSuccess() {
		//given
		Long groupId = mockGroup.getId();
		GroupMemberSaveRequest request = mock(GroupMemberSaveRequest.class);
		GroupMember expectedMember = GroupMember.builder()
			.name("김반숙")
			.group(mockGroup)
			.role(ExpenseRole.PARTICIPANT)
			.build();

		when(groupMemberUpdater.addToGroup(eq(groupId), any(GroupMemberSaveRequest.class))).thenReturn(expectedMember);

		//when
		GroupMemberResponse response = commandGroupMemberService.addGroupMember(groupId, request);

		//then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("김반숙");
		verify(groupMemberUpdater, times(1)).addToGroup(eq(groupId), any(GroupMemberSaveRequest.class));
	}

	@DisplayName("참여자 입금 내역을 업데이트 할 수 있다.")
	@Test
	void updatePaymentStatus_Success() {
		//given
		GroupMember expectedGroupMember = GroupMember.builder()
			.name("김반숙")
			.group(mockGroup)
			.isPaid(true)
			.role(ExpenseRole.PARTICIPANT)
			.build();
		PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest(true);
		when(groupMemberUpdater.updatePaymentStatus(any(), eq(request))).thenReturn(expectedGroupMember);

		//then
		GroupMemberResponse response = commandGroupMemberService.updatePaymentStatus(1L, request);

		//then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("김반숙");
		assertThat(response.role()).isEqualTo(ExpenseRole.PARTICIPANT);
		assertThat(response.isPaid()).isTrue();

		verify(groupMemberUpdater, times(1)).updatePaymentStatus(any(), eq(request));
	}
}
