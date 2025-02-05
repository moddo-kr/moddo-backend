package com.dnd.moddo.domain.memberExpense.entity;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member-expenses")
@Entity
public class MemberExpense {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "expense_id")
	private Expense expense;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_member_id")
	private GroupMember groupMember;

	private Long amount;

	public MemberExpense(Expense expense, GroupMember groupMember, Long amount) {
		this.expense = expense;
		this.groupMember = groupMember;
		this.amount = amount;
	}
}

