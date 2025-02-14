package com.dnd.moddo.domain.memberExpense.entity;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_expenses")
@Entity
public class MemberExpense {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "expense_id")
	private Long expenseId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_member_id")
	private GroupMember groupMember;

	private Long amount;

	@Builder
	public MemberExpense(Long expenseId, GroupMember groupMember, Long amount) {
		this.expenseId = expenseId;
		this.groupMember = groupMember;
		this.amount = amount;
	}

	public void updateAmount(Long amount) {
		this.amount = amount;
	}
}

