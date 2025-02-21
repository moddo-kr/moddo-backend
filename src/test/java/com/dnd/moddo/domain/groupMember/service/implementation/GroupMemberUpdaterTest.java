package com.dnd.moddo.domain.groupMember.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import com.dnd.moddo.domain.groupMember.dto.request.GroupMemberSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.groupMember.exception.GroupMemberDuplicateNameException;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;
import com.dnd.moddo.global.config.S3Bucket;

@ExtendWith(MockitoExtension.class)
class GroupMemberUpdaterTest {
	@Mock
	private GroupMemberRepository groupMemberRepository;
	@Mock
	private GroupMemberReader groupMemberReader;
	@Mock
	private GroupMemberValidator groupMemberValidator;
	@Mock
	private GroupReader groupReader;
	@Mock
	private S3Bucket s3Bucket;
	@InjectMocks
	private GroupMemberUpdater groupMemberUpdater;

	private Group mockGroup;

	@BeforeEach
	void setup() {
		mockGroup = mock(Group.class);
	}

	@DisplayName("추가하려는 참여자의 이름이 기존 참여자의 이름과 중복되지 않을 경우 참여자 추가에 성공한다.")
	@Test
	void addToGroupSuccess() {
		// given
		Long groupId = 1L;
		GroupMemberSaveRequest request = mock(GroupMemberSaveRequest.class);
		String newMemberName = "김반숙";

		when(request.name()).thenReturn(newMemberName);
		when(groupReader.read(eq(groupId))).thenReturn(mockGroup);

		List<GroupMember> mockGroupMembers = new ArrayList<>();
		when(groupMemberReader.findAllByGroupId(eq(groupId))).thenReturn(mockGroupMembers);

		doNothing().when(groupMemberValidator).validateMemberNamesNotDuplicate(any());

		GroupMember expectedGroupMember = GroupMember.builder()
			.name(newMemberName)
			.group(mockGroup)
			.role(ExpenseRole.PARTICIPANT)
			.profileId(1)
			.build();
		when(groupMemberRepository.save(any())).thenReturn(expectedGroupMember);

		// when
		GroupMember result = groupMemberUpdater.addToGroup(groupId, request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getGroup()).isEqualTo(mockGroup);
		assertThat(result.getName()).isEqualTo(newMemberName);
		assertThat(result.getProfileId()).isEqualTo(1);

		verify(groupMemberRepository, times(1)).save(any());
	}

	@DisplayName("추가하려는 참여자의 이름이 기존 참여자의 이름과 중복되는 경우 예외가 발생한다.")
	@Test
	void addToGroupDuplicatedName() {
		// given
		Long groupId = 1L;
		GroupMemberSaveRequest request = mock(GroupMemberSaveRequest.class);
		String duplicatedName = "김반숙";

		when(request.name()).thenReturn(duplicatedName);
		when(groupReader.read(eq(groupId))).thenReturn(mockGroup);

		List<GroupMember> mockGroupMembers = new ArrayList<>();
		GroupMember existingMember = GroupMember.builder().name(duplicatedName).build();
		mockGroupMembers.add(existingMember);
		when(groupMemberReader.findAllByGroupId(eq(groupId))).thenReturn(mockGroupMembers);

		doThrow(new GroupMemberDuplicateNameException()).when(groupMemberValidator)
			.validateMemberNamesNotDuplicate(any());

		// when & then
		assertThatThrownBy(() -> {
			groupMemberUpdater.addToGroup(groupId, request);
		}).hasMessage("중복된 참여자의 이름은 저장할 수 없습니다.");
	}

	@DisplayName("참여자가 유효할 때 참여자의 입금 상태를 변경할 수 있다.")
	@Test
	void updatePaymentStatus_Success() {
		// given
		GroupMember groupMember = GroupMember.builder()
			.name("김반숙")
			.group(mockGroup)
			.role(ExpenseRole.PARTICIPANT)
			.build();
		PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest(true);

		when(groupMemberRepository.getById(any())).thenReturn(groupMember);

		// when
		GroupMember result = groupMemberUpdater.updatePaymentStatus(1L, request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.isPaid()).isTrue();
	}

	@DisplayName("9번째 이상의 참여자가 추가될 때 프로필 ID가 올바르게 순환된다.")
	@Test
	void addToGroupProfileRotationSuccess() {
		// given
		Long groupId = 1L;
		GroupMemberSaveRequest request = mock(GroupMemberSaveRequest.class);
		String newMemberName = "김철수";

		when(request.name()).thenReturn(newMemberName);
		when(groupReader.read(eq(groupId))).thenReturn(mockGroup);

		// 기존 멤버 8명 설정
		List<GroupMember> mockGroupMembers = new ArrayList<>();
		for (int i = 1; i <= 8; i++) {
			mockGroupMembers.add(
				GroupMember.builder()
					.name("멤버" + i)
					.group(mockGroup)
					.profileId(i)
					.role(ExpenseRole.PARTICIPANT)
					.build()
			);
		}
		when(groupMemberReader.findAllByGroupId(eq(groupId))).thenReturn(mockGroupMembers);

		doNothing().when(groupMemberValidator).validateMemberNamesNotDuplicate(any());

		GroupMember expectedGroupMember = GroupMember.builder()
			.name(newMemberName)
			.group(mockGroup)
			.role(ExpenseRole.PARTICIPANT)
			.profileId(1)
			.build();
		when(groupMemberRepository.save(any())).thenReturn(expectedGroupMember);

		// when
		GroupMember result = groupMemberUpdater.addToGroup(groupId, request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getGroup()).isEqualTo(mockGroup);
		assertThat(result.getName()).isEqualTo(newMemberName);
		assertThat(result.getProfileId()).isEqualTo(1);

		verify(groupMemberRepository, times(1)).save(any());
	}
}
