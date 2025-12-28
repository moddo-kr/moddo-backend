package com.dnd.moddo.domain.appointmentMember.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.member.exception.MemberNotFoundException;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.infrastructure.MemberRepository;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
public class MemberReaderTest {
	@Mock
	private MemberRepository memberRepository;
	@InjectMocks
	private MemberReader memberReader;

	private Settlement mockSettlement;

	@BeforeEach
	void setUp() {
		mockSettlement = GroupTestFactory.createDefault();
	}

	@DisplayName("모임이 존재할때 모임의 모든 참여자를 조회에 성공한다.")
	@Test
	void findAllBySettlementIdSuccess() {
		//given
		Long groupId = mockSettlement.getId();
		List<Member> expectedMembers = List.of(
			Member.builder()
				.name("김모또")
				.settlement(mockSettlement)
				.role(ExpenseRole.MANAGER)
				.isPaid(false)
				.build(),
			Member.builder()
				.name("김반숙")
				.settlement(mockSettlement)
				.role(ExpenseRole.PARTICIPANT)
				.isPaid(false)
				.build()
		);

		when(memberRepository.findBySettlementId(eq(groupId))).thenReturn(expectedMembers);

		//when
		List<Member> result = memberReader.findAllBySettlementId(groupId);

		//then
		assertThat(result).isNotNull();
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(0).getName()).isEqualTo("김모또");
		assertThat(result.get(0).getRole()).isEqualTo(ExpenseRole.MANAGER);
		verify(memberRepository, times(1)).findBySettlementId(groupId);
	}

	@DisplayName("참여자가 존재할때 참여자 id를 사용해 참여자 정보 조회에 성공한다.")
	@Test
	void findByGroupMemberIdSuccess() {
		//given
		Long groupMemberId = 1L;
		Member expectedMember = Member.builder()
			.name("김반숙")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.isPaid(false)
			.build();

		when(memberRepository.getById(eq(groupMemberId))).thenReturn(expectedMember);

		//when
		Member result = memberReader.findByAppointmentMemberId(groupMemberId);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getSettlement()).isEqualTo(mockSettlement);
		assertThat(result.getName()).isEqualTo("김반숙");
		verify(memberRepository, times(1)).getById(eq(groupMemberId));

	}

	@DisplayName("참여자가 존재하지 않을때 참여자 id를 사용해 조회하려고 하면 예외가 발생한다.")
	@Test
	void findByGroupMemberIdFail() {
		//given
		Long appointmentMember = 1L;
		doThrow(new MemberNotFoundException(appointmentMember)).when(memberRepository)
			.getById(eq(appointmentMember));

		//when & then
		assertThatThrownBy(() -> {
			memberReader.findByAppointmentMemberId(appointmentMember);
		}).hasMessage("해당 참여자를 찾을 수 없습니다. (AppointmentMember ID: " + appointmentMember + ")");
	}

}
