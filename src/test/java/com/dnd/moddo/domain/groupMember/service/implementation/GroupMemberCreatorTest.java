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
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
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
	private GroupMemberValidator groupMemberValidator;
	@Mock
	private GroupReader groupReader;
	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private GroupMemberCreator groupMemberCreator;

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = mock(Group.class);
	}

	@DisplayName("사용자가 비회원인 경우, 모든 이름이 중복없이 유효할 때 총무의 이름은 '김모또'로 생성된다.")
	@Test
	void create_Success_WithGuestMember() {
		//given
		Long groupId = 1L, userId = 1L;
		Group mockGroup = mock(Group.class);

		User mockUser = mock(User.class);
		when(userRepository.getById(eq(userId))).thenReturn(mockUser);
		when(mockUser.getIsMember()).thenReturn(false);

		GroupMember expectedMember = new GroupMember("김모또", 1, mockGroup, ExpenseRole.MANAGER);

		when(groupMemberRepository.save(any())).thenReturn(expectedMember);

		//when
		GroupMember savedMember = groupMemberCreator.createManagerForGroup(mockGroup, userId);

		//then
		assertThat(savedMember).isNotNull();
		assertThat(savedMember.getName()).isEqualTo("김모또");
		assertThat(savedMember.getRole()).isEqualTo(ExpenseRole.MANAGER);
		verify(groupMemberRepository, times(1)).save(any());
	}

	@DisplayName("사용자가 회원인 경우, 모든 이름이 중복없이 유효할 때 총무의 이름은 회원의 이름으로 생성된다.")
	@Test
	void create_Success_WithMember() {
		//given
		Long userId = 1L;
		Group mockGroup = mock(Group.class);

		User mockUser = mock(User.class);
		when(userRepository.getById(eq(userId))).thenReturn(mockUser);
		when(mockUser.getIsMember()).thenReturn(true);
		when(mockUser.getName()).thenReturn("연노른자");

		GroupMember expectedMember = new GroupMember("연노른자", 1, mockGroup, ExpenseRole.MANAGER);
		when(groupMemberRepository.save(any(GroupMember.class))).thenReturn(expectedMember);

		//when
		GroupMember savedMember = groupMemberCreator.createManagerForGroup(mockGroup, userId);

		//then
		assertThat(savedMember).isNotNull();
		assertThat(savedMember.getName()).isEqualTo("연노른자");
		assertThat(savedMember.getRole()).isEqualTo(ExpenseRole.MANAGER);

		verify(userRepository, times(1)).getById(eq(userId));
		verify(groupMemberRepository, times(1)).save(any());
	}
}