package com.dnd.moddo.domain.expense.entity;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.domain.group.entity.Group;

class ExpenseTest {

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1),
			"은행", "계좌");
	}

	@Test
	void update() {
		//given
		Long initAmount = 20000L;
		String initContent = "old content";
		LocalDate initDate = LocalDate.of(2025, 02, 03);
		Expense expense = new Expense(mockGroup, initAmount, initContent, 0, initDate);

		// when
		Long newAmount = 30000L;
		String newContent = "new content";
		LocalDate newDate = LocalDate.of(2025, 02, 02);

		expense.update(newAmount, newContent, newDate);

		// then
		assertThat(expense.getAmount()).isEqualTo(newAmount);
		assertThat(expense.getContent()).isEqualTo(newContent);
		assertThat(expense.getDate()).isEqualTo(newDate);
	}
}