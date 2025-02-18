package com.dnd.moddo.domain.groupMember.service.implementation;

import static org.assertj.core.api.BDDAssertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMembersSaveRequest;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.groupMember.exception.GroupMemberDuplicateNameException;
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

	private GroupMembersSaveRequest request;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1),
			"은행", "계좌");
		request = new GroupMembersSaveRequest(new ArrayList<>());
	}

	@DisplayName("사용자가 비회원인 경우, 모든 이름이 중복없이 유효할 때 총무의 이름은 '김모또'로 추가되고, 다른 참여자는 정상적으로 추가된다.")
	@Test
	void create_Success_WithGuestMember() {
		//given
		Long groupId = mockGroup.getId(), userId = 1L;

		when(groupReader.read(eq(groupId))).thenReturn(mockGroup);

		doNothing().when(groupMemberValidator).validateMemberNamesNotDuplicate(any());

		User mockUser = mock(User.class);
		when(userRepository.getById(eq(userId))).thenReturn(mockUser);
		when(mockUser.getIsMember()).thenReturn(false);

		List<GroupMember> expectedMembers = List.of(
			new GroupMember("김모또", 1, mockGroup, ExpenseRole.MANAGER),
			new GroupMember("김반숙", 2, mockGroup, ExpenseRole.PARTICIPANT)
		);

		when(groupMemberRepository.saveAll(anyList())).thenReturn(expectedMembers);

		//when
		List<GroupMember> savedMembers = groupMemberCreator.create(groupId, userId, request);

		//then
		assertThat(savedMembers).isNotNull();
		assertThat(savedMembers.size()).isEqualTo(2);
		assertThat(savedMembers.get(0).getName()).isEqualTo("김모또");
		assertThat(savedMembers.get(0).getRole()).isEqualTo(ExpenseRole.MANAGER);
		verify(groupMemberRepository, times(1)).saveAll(anyList());
	}

	@DisplayName("사용자가 회원인 경우, 모든 이름이 중복없이 유효할 때 총무의 이름은 회원의 이름으로 추가되고, 다른 참여자는 정상적으로 추가된다.")
	@Test
	void create_Success_WithMember() {
		//given
		Long groupId = mockGroup.getId(), userId = 1L;

		when(groupReader.read(eq(groupId))).thenReturn(mockGroup);

		doNothing().when(groupMemberValidator).validateMemberNamesNotDuplicate(any());

		User mockUser = mock(User.class);
		when(userRepository.getById(eq(userId))).thenReturn(mockUser);
		when(mockUser.getIsMember()).thenReturn(true);
		when(mockUser.getName()).thenReturn("연노른자");

		List<GroupMember> expectedMembers = List.of(
			new GroupMember("연노른자", 1, mockGroup, ExpenseRole.MANAGER),
			new GroupMember("김반숙", 2, mockGroup, ExpenseRole.PARTICIPANT)
		);

		when(groupMemberRepository.saveAll(anyList())).thenReturn(expectedMembers);

		//when
		List<GroupMember> savedMembers = groupMemberCreator.create(groupId, userId, request);

		//then
		assertThat(savedMembers).isNotNull();
		assertThat(savedMembers.size()).isEqualTo(2);
		assertThat(savedMembers.get(0).getName()).isEqualTo("연노른자");
		assertThat(savedMembers.get(0).getRole()).isEqualTo(ExpenseRole.MANAGER);
		verify(groupMemberRepository, times(1)).saveAll(anyList());
	}

	@DisplayName("요청에 중복된 이름이 존재할 경우 참여자를 추가하면 예외가 발생한다.")
	@Test
	void create_WithThrow_ByDuplicatedName() {

		//given
		Long groupId = mockGroup.getId(), userId = 1L;
		List<GroupMember> groupMembers = new ArrayList<>();

		when(groupReader.read(eq(groupId))).thenReturn(mockGroup);

		doThrow(new GroupMemberDuplicateNameException()).when(groupMemberValidator)
			.validateMemberNamesNotDuplicate(any());

		//when & then
		assertThatThrownBy(() -> {
			groupMemberCreator.create(groupId, userId, request);
		}).hasMessage("중복된 참여자의 이름은 저장할 수 없습니다.");

	}
}