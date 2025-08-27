package com.dnd.moddo.domain.appointmentMember.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMembersResponse;
import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.appointmentMember.service.implementation.AppointmentMemberReader;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
public class QueryAppointmentMemberServiceTest {

	@Mock
	private AppointmentMemberReader appointmentMemberReader;
	@InjectMocks
	private QueryAppointmentMemberService queryAppointmentMemberService;

	private Settlement mockSettlement;
	private List<AppointmentMember> mockMembers;

	@BeforeEach
	void setUp() {
		mockSettlement = GroupTestFactory.createDefault();

		mockMembers = List.of(
			AppointmentMember.builder()
				.name("김모또")
				.settlement(mockSettlement)
				.profileId(0)
				.role(ExpenseRole.MANAGER)
				.build(),
			AppointmentMember.builder()
				.name("김반숙")
				.profileId(1)
				.settlement(mockSettlement)
				.role(ExpenseRole.PARTICIPANT)
				.build()
		);
	}

	@DisplayName("모임이 존재하면 모임의 모든 참여자를 조회할 수 있다.")
	@Test
	void findAll() {
		//given
		Long groupId = mockSettlement.getId();

		when(appointmentMemberReader.findAllByGroupId(eq(groupId))).thenReturn(mockMembers);

		//when
		AppointmentMembersResponse response = queryAppointmentMemberService.findAll(groupId);

		//then
		assertThat(response).isNotNull();
		assertThat(response.members().size()).isEqualTo(2);
		assertThat(response.members().get(0).name()).isEqualTo("김모또");
		verify(appointmentMemberReader, times(1)).findAllByGroupId(eq(groupId));
	}
}
