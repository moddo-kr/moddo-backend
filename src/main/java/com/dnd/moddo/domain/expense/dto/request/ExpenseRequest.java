package com.dnd.moddo.domain.expense.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.memberExpense.dto.request.MemberExpenseRequest;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ExpenseRequest(
	@Positive(message = "지출내역 값은 양수여야 합니다.")
	@Max(value = 5_000_000, message = "지출내역 값은 최대 500만원까지 입력할 수 있습니다.")
	Long amount,

	@NotBlank(message = "지출 장소 및 내용은 필수 입력값 입니다.")
	String content,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate date,
	@Valid List<MemberExpenseRequest> memberExpenses

) {

	public Expense toEntity(Settlement settlement) {
		return new Expense(settlement, amount(), content(), date());
	}
}
