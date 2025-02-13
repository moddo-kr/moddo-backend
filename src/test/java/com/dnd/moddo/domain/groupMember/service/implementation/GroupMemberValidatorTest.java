package com.dnd.moddo.domain.groupMember.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.domain.groupMember.dto.request.GroupMemberSaveRequest;

class GroupMemberValidatorTest {
	private final GroupMemberValidator groupMemberValidator = new GroupMemberValidator();

	@DisplayName("중복된 이름이 없는 경우 검증에 성공한다.")
	@Test
	void validateGroupMemberNamesNotDuplicate() {
		List<String> names = List.of("김반숙", "이계란", "박완숙");

		assertThatCode(() -> {
			groupMemberValidator.validateMemberNamesNotDuplicate(names);
		}).doesNotThrowAnyException();
	}

	@DisplayName("중복된 이름이 있는 경우 검증에 실패하여 예외가 발생한다.")
	@Test
	void validateGroupMemberNamesIsDuplicate() {
		List<String> names = List.of("김반숙", "이계란", "김반숙");

		assertThatThrownBy(() -> {
			groupMemberValidator.validateMemberNamesNotDuplicate(names);
		}).hasMessage("중복된 참여자의 이름은 저장할 수 없습니다.");
	}

	@DisplayName("참여자 역할에 정산담당자가 존재하면 성공한다.")
	@Test
	void validateParticipant_Success() {
		List<GroupMemberSaveRequest> members = List.of(new GroupMemberSaveRequest("김모또", "MANAGER"),
			new GroupMemberSaveRequest("김반숙", "PARTICIPANT"));

		assertThatCode(() -> {
			groupMemberValidator.validateManagerExists(members);
		}).doesNotThrowAnyException();
	}

	@DisplayName("참여자 역할에 정산담당자가 존재하지 않으면 예외가 발생한다.")
	@Test
	void validateParticipant_ThrowException_WhenNoManagerPresent() {
		List<GroupMemberSaveRequest> members = List.of(new GroupMemberSaveRequest("김모또", "PARTICIPANT"),
			new GroupMemberSaveRequest("김반숙", "PARTICIPANT"));

		assertThatThrownBy(() -> {
			groupMemberValidator.validateManagerExists(members);
		}).hasMessage("총무(MANAGER)는 한 명 있어야 합니다.");
	}
}