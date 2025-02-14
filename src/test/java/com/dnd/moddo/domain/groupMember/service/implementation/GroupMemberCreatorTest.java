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
import com.dnd.moddo.domain.groupMember.exception.InvalidExpenseParticipantsException;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;

@ExtendWith(MockitoExtension.class)
public class GroupMemberCreatorTest {
	@Mock
	private GroupMemberRepository groupMemberRepository;
	@Mock
	private GroupMemberValidator groupMemberValidator;
	@Mock
	private GroupReader groupReader;
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

	@DisplayName("모든 이름이 중복없이 유효할때 참여자를 추가하면 성공한다.")
	@Test
	void createSuccess() {
		//given
		Long groupId = mockGroup.getId();

		when(groupReader.read(eq(groupId))).thenReturn(mockGroup);
		doNothing().when(groupMemberValidator).validateManagerExists(any());
		doNothing().when(groupMemberValidator).validateMemberNamesNotDuplicate(any());

		List<GroupMember> expectedMembers = List.of(
			new GroupMember("김모또", 1, mockGroup, ExpenseRole.MANAGER),
			new GroupMember("김반숙", 2, mockGroup, ExpenseRole.PARTICIPANT)
		);

		when(groupMemberRepository.saveAll(anyList())).thenReturn(expectedMembers);

		//when
		List<GroupMember> savedMembers = groupMemberCreator.create(groupId, request);

		//then
		assertThat(savedMembers).isNotNull();
		assertThat(savedMembers.size()).isEqualTo(2);
		assertThat(savedMembers.get(0).getName()).isEqualTo("김모또");
		assertThat(savedMembers.get(0).getRole()).isEqualTo(ExpenseRole.MANAGER);
		verify(groupMemberRepository, times(1)).saveAll(anyList());
	}

	@DisplayName("요청에 중복된 이름이 존재할 경우 참여자를 추가하면 예외가 발생한다.")
	@Test
	void createDuplicatedName() {

		//given
		Long groupId = mockGroup.getId();
		List<GroupMember> groupMembers = new ArrayList<>();

		when(groupReader.read(eq(groupId))).thenReturn(mockGroup);

		doThrow(new GroupMemberDuplicateNameException()).when(groupMemberValidator)
			.validateMemberNamesNotDuplicate(any());

		//when & then
		assertThatThrownBy(() -> {
			groupMemberCreator.create(groupId, request);
		}).hasMessage("중복된 참여자의 이름은 저장할 수 없습니다.");

	}

	@DisplayName("추가하려는 참여자중 정산담당자가 포함되어있지 않으면 예외가 발생한다.")
	@Test
	void createGroupMember_ThrowException_WhenNoManagerPresent() {

		//given
		Long groupId = mockGroup.getId();
		List<GroupMember> groupMembers = new ArrayList<>();

		when(groupReader.read(eq(groupId))).thenReturn(mockGroup);

		doThrow(new InvalidExpenseParticipantsException()).when(groupMemberValidator)
			.validateManagerExists(any());

		//when & then
		assertThatThrownBy(() -> {
			groupMemberCreator.create(groupId, request);
		}).hasMessage("총무(MANAGER)는 한 명 있어야 합니다.");

	}
}