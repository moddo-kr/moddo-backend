package com.dnd.moddo.domain.expense.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.event.domain.expense.Expense;
import com.dnd.moddo.event.domain.expense.exception.ExpenseNotSettlementException;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.global.support.GroupTestFactory;

class ExpenseTest {

	private Settlement mockSettlement;

	@BeforeEach
	void setUp() {
		mockSettlement = GroupTestFactory.createDefault();
	}

	@DisplayName("지출 내역 정보를 수정할 수 있다.")
	@Test
	void update() {
		//given
		Long initAmount = 20000L;
		String initContent = "old content";
		LocalDate initDate = LocalDate.of(2025, 02, 03);
		Expense expense = new Expense(mockSettlement, initAmount, initContent, initDate);

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

	@DisplayName("지출 이미지 URL을 업데이트할 수 있다.")
	@Test
	void updateImgUrl() {
		// given
		Long initAmount = 20000L;
		String initContent = "old content";
		LocalDate initDate = LocalDate.of(2025, 02, 03);
		Expense expense = new Expense(mockSettlement, initAmount, initContent, initDate);
		List<String> images = List.of("image1.jpg", "image2.jpg");
		expense.updateImgUrl(images);

		// when
		List<String> newImages = List.of("new_image1.jpg", "new_image2.jpg", "new_image3.jpg");
		expense.updateImgUrl(newImages);

		// then
		assertThat(expense.getImages()).isEqualTo(newImages);
	}

	@DisplayName("지출 내역이 해당 정산에 속하는지 검증에 성공한다.")
	@Test
	void validateSettlementSuccess() {
		// given
		Long settlementId = 1L;
		Settlement mockSettlement = new Settlement(settlementId, 1L, "정산", null, null, null, null, null, null, 1L,
			"code");

		Expense expense = Expense.builder()
			.settlement(mockSettlement)
			.amount(10000L)
			.content("테스트")
			.date(LocalDate.now())
			.build();

		// when & then
		assertThatCode(() -> expense.validateSettlement(settlementId))
			.doesNotThrowAnyException();
	}

	@DisplayName("지출 내역이 해당 정산에 속하지 않으면 예외가 발생한다.")
	@Test
	void validateSettlementFail() {
		// given
		Long settlementId = 1L;
		Long otherSettlementId = 999L;
		Settlement mockSettlement = new Settlement(settlementId, 1L, "정산", null, null, null, null, null, null, 1L,
			"code");

		Expense expense = Expense.builder()
			.settlement(mockSettlement)
			.amount(10000L)
			.content("테스트")
			.date(LocalDate.now())
			.build();

		// when & then
		assertThatThrownBy(() -> expense.validateSettlement(otherSettlementId))
			.isInstanceOf(ExpenseNotSettlementException.class);
	}
}