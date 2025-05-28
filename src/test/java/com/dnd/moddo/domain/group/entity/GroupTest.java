package com.dnd.moddo.domain.group.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.domain.group.dto.request.GroupAccountRequest;
import com.dnd.moddo.support.GroupTestFactory;

class GroupTest {

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = GroupTestFactory.createDefault();
	}

	@DisplayName("그룹의 작성자(userId)가 해당 그룹의 작성자인지 확인한다.")
	@Test
	void testIsWriter_whenUserIsWriter() {
		// given
		Long userId = 1L;

		// when
		boolean isWriter = mockGroup.isWriter(userId);

		// then
		assertThat(isWriter).isTrue();
	}

	@DisplayName("그룹의 작성자가 아닌 경우 false를 반환한다.")
	@Test
	void testIsWriter_whenUserIsNotWriter() {
		// given
		Long userId = 2L;

		// when
		boolean isWriter = mockGroup.isWriter(userId);

		// then
		assertThat(isWriter).isFalse();
	}

	@DisplayName("그룹의 계좌 정보를 업데이트할 수 있다.")
	@Test
	void testUpdateAccount() {
		// given
		GroupAccountRequest request = new GroupAccountRequest("새로운 은행", "새로운 계좌");

		// when
		mockGroup.updateAccount(request);

		// then
		assertThat(mockGroup.getBank()).isEqualTo("새로운 은행");
		assertThat(mockGroup.getAccountNumber()).isEqualTo("새로운 계좌");
		assertThat(mockGroup.getDeadline()).isAfter(LocalDateTime.now());
	}

	@DisplayName("그룹의 작성자를 확인할 수 있다.")
	@Test
	void testGroupWriter() {
		// given
		Long writerId = 1L;

		// when
		Long actualWriterId = mockGroup.getWriter();

		// then
		assertThat(actualWriterId).isEqualTo(writerId);
	}
}
