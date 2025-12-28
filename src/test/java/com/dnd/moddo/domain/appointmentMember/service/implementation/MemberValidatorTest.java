package com.dnd.moddo.domain.appointmentMember.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.event.application.impl.MemberValidator;

class MemberValidatorTest {
	private final MemberValidator memberValidator = new MemberValidator();

	@DisplayName("중복된 이름이 없는 경우 검증에 성공한다.")
	@Test
	void validateGroupMemberNamesNotDuplicate() {
		List<String> names = List.of("김반숙", "이계란", "박완숙");

		assertThatCode(() -> {
			memberValidator.validateMemberNamesNotDuplicate(names);
		}).doesNotThrowAnyException();
	}

	@DisplayName("중복된 이름이 있는 경우 검증에 실패하여 예외가 발생한다.")
	@Test
	void validateGroupMemberNamesIsDuplicate() {
		List<String> names = List.of("김반숙", "이계란", "김반숙");

		assertThatThrownBy(() -> {
			memberValidator.validateMemberNamesNotDuplicate(names);
		}).hasMessage("중복된 참여자의 이름은 저장할 수 없습니다.");
	}

}