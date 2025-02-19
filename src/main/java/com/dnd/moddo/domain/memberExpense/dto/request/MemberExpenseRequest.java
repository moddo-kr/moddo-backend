package com.dnd.moddo.domain.memberExpense.dto.request;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MemberExpenseRequest(
	@NotNull(message = "참여자 id는 필수 항목입니다.")
	@Positive(message = "참여자 id는 양수여야 합니다.")
	Long memberId,

	@Positive(message = "지출내역 값은 양수여야 합니다.")
	@Max(value = 5_000_000, message = "지출내역 값은 최대 500만원까지 입력할 수 있습니다.")
	Long amount) {
	public MemberExpense toEntity(Long expenseId, GroupMember groupMember) {
		return MemberExpense.builder()
			.expenseId(expenseId)
			.groupMember(groupMember)
			.amount(amount())
			.build();
	}
}
