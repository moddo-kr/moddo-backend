package com.dnd.moddo.domain.expense.entity;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ExpenseTest {

	@Test
	void update() {
		//given
		Long initAmount = 20000L;
		String initContent = "old content";
		LocalDate initDate = LocalDate.of(2025, 02, 03);
		Expense expense = new Expense(1L, initAmount, initContent, initDate);

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