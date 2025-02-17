package com.dnd.moddo.domain.expense.entity;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
		Expense expense = new Expense(mockGroup, initAmount, initContent, initDate);

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

	@Test
	void updateImgUrl() {
		// given
		Long initAmount = 20000L;
		String initContent = "old content";
		LocalDate initDate = LocalDate.of(2025, 02, 03);
		Expense expense = new Expense(mockGroup, initAmount, initContent, initDate);
		List<String> images = List.of("image1.jpg", "image2.jpg");
		expense.updateImgUrl(images);

		// when
		List<String> newImages = List.of("new_image1.jpg", "new_image2.jpg", "new_image3.jpg");
		expense.updateImgUrl(newImages);

		// then
		assertThat(expense.getImages()).isEqualTo(newImages);
	}
}