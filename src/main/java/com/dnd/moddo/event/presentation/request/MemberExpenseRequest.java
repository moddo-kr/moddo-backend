package com.dnd.moddo.event.presentation.request;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.memberExpense.MemberExpense;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MemberExpenseRequest(
	@NotNull(message = "참여자 id는 필수 항목입니다.")
	@Positive(message = "참여자 id는 양수여야 합니다.")
	Long id,

	@Positive(message = "지출내역 값은 양수여야 합니다.")
	@Max(value = 5_000_000, message = "지출내역 값은 최대 500만원까지 입력할 수 있습니다.")
	Long amount) {
	public MemberExpense toEntity(Long expenseId, Member member) {
		return MemberExpense.builder()
			.expenseId(expenseId)
			.member(member)
			.amount(amount())
			.build();
	}
}
