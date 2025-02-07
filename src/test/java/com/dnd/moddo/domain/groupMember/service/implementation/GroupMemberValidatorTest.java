package com.dnd.moddo.domain.groupMember.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
	void validateGroupMemberManesIsDuplicate() {
		List<String> names = List.of("김반숙", "이계란", "김반숙");

		assertThatThrownBy(() -> {
			groupMemberValidator.validateMemberNamesNotDuplicate(names);
		}).hasMessage("중복된 참여자의 이름은 저장할 수 없습니다.");
	}
}