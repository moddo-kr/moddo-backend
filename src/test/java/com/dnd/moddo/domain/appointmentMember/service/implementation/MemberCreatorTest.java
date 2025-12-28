package com.dnd.moddo.domain.appointmentMember.service.implementation;

import static org.assertj.core.api.BDDAssertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;
import com.dnd.moddo.event.application.impl.MemberCreator;
import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.infrastructure.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class MemberCreatorTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private MemberCreator memberCreator;

	private Settlement mockSettlement;

	@BeforeEach
	void setUp() {
		mockSettlement = mock(Settlement.class);
	}

	@DisplayName("사용자가 비회원인 경우, 총무의 이름은 '김모또'로 생성되고 프로필 ID가 0으로 설정된다.")
	@Test
	void create_Success_WithGuestMember() {
		// given
		Long userId = 1L;
		User mockUser = mock(User.class);

		when(userRepository.getById(eq(userId))).thenReturn(mockUser);
		when(mockUser.getIsMember()).thenReturn(false);

		Member expectedMember = Member.builder()
			.name("김모또")
			.settlement(mockSettlement)
			.profileId(0)
			.role(ExpenseRole.MANAGER)
			.build();

		when(memberRepository.save(any(Member.class))).thenReturn(expectedMember);

		// when
		Member savedMember = memberCreator.createManagerForSettlement(mockSettlement, userId);

		// then
		assertThat(savedMember).isNotNull();
		assertThat(savedMember.getName()).isEqualTo("김모또");
		assertThat(savedMember.getRole()).isEqualTo(ExpenseRole.MANAGER);
		assertThat(savedMember.getProfileId()).isEqualTo(0); // profileId 검증

		verify(userRepository, times(1)).getById(eq(userId));
		verify(memberRepository, times(1)).save(any(Member.class));
	}

	@DisplayName("사용자가 회원인 경우, 총무의 이름은 회원의 이름으로 생성되고 프로필 ID가 0으로 설정된다.")
	@Test
	void create_Success_WithMember() {
		// given
		Long userId = 1L;
		User mockUser = mock(User.class);

		when(userRepository.getById(eq(userId))).thenReturn(mockUser);
		when(mockUser.getIsMember()).thenReturn(true);
		when(mockUser.getName()).thenReturn("연노른자");

		Member expectedMember = Member.builder()
			.name("연노른자")
			.settlement(mockSettlement)
			.profileId(0)
			.role(ExpenseRole.MANAGER)
			.build();

		when(memberRepository.save(any(Member.class))).thenReturn(expectedMember);

		// when
		Member savedMember = memberCreator.createManagerForSettlement(mockSettlement, userId);

		// then
		assertThat(savedMember).isNotNull();
		assertThat(savedMember.getName()).isEqualTo("연노른자");
		assertThat(savedMember.getRole()).isEqualTo(ExpenseRole.MANAGER);
		assertThat(savedMember.getProfileId()).isEqualTo(0);

		verify(userRepository, times(1)).getById(eq(userId));
		verify(memberRepository, times(1)).save(any(Member.class));
	}
}
