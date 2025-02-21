package com.dnd.moddo.domain.groupMember.service.implementation;

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

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class GroupMemberCreatorTest {

	@Mock
	private GroupMemberRepository groupMemberRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private GroupMemberCreator groupMemberCreator;

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = mock(Group.class);
	}

	@DisplayName("사용자가 비회원인 경우, 총무의 이름은 '김모또'로 생성되고 프로필 URL이 동적으로 생성된다.")
	@Test
	void create_Success_WithGuestMember() {
		// given
		Long userId = 1L;
		User mockUser = mock(User.class);

		when(userRepository.getById(eq(userId))).thenReturn(mockUser);
		when(mockUser.getIsMember()).thenReturn(false);

		GroupMember expectedMember = GroupMember.builder()
			.name("김모또")
			.group(mockGroup)
			.profileId(null)
			.role(ExpenseRole.MANAGER)
			.build();

		when(groupMemberRepository.save(any(GroupMember.class))).thenReturn(expectedMember);

		// when
		GroupMember savedMember = groupMemberCreator.createManagerForGroup(mockGroup, userId);

		// then
		assertThat(savedMember).isNotNull();
		assertThat(savedMember.getName()).isEqualTo("김모또");
		assertThat(savedMember.getRole()).isEqualTo(ExpenseRole.MANAGER);
		assertThat(savedMember.getProfileUrl()).isEqualTo("https://moddo-s3.s3.amazonaws.com/profile/MODDO.png");

		verify(userRepository, times(1)).getById(eq(userId));
		verify(groupMemberRepository, times(1)).save(any(GroupMember.class));
	}

	@DisplayName("사용자가 회원인 경우, 총무의 이름은 회원의 이름으로 생성되고 프로필 URL이 동적으로 생성된다.")
	@Test
	void create_Success_WithMember() {
		// given
		Long userId = 1L;
		User mockUser = mock(User.class);

		when(userRepository.getById(eq(userId))).thenReturn(mockUser);
		when(mockUser.getIsMember()).thenReturn(true);
		when(mockUser.getName()).thenReturn("연노른자");

		GroupMember expectedMember = GroupMember.builder()
			.name("연노른자")
			.group(mockGroup)
			.profileId(0)
			.role(ExpenseRole.MANAGER)
			.build();

		when(groupMemberRepository.save(any(GroupMember.class))).thenReturn(expectedMember);

		// when
		GroupMember savedMember = groupMemberCreator.createManagerForGroup(mockGroup, userId);

		// then
		assertThat(savedMember).isNotNull();
		assertThat(savedMember.getName()).isEqualTo("연노른자");
		assertThat(savedMember.getRole()).isEqualTo(ExpenseRole.MANAGER);
		assertThat(savedMember.getProfileUrl()).isEqualTo("https://moddo-s3.s3.amazonaws.com/profile/MODDO.png");

		verify(userRepository, times(1)).getById(eq(userId));
		verify(groupMemberRepository, times(1)).save(any(GroupMember.class));
	}
}
