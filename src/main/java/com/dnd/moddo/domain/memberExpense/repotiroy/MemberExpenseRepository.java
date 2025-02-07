package com.dnd.moddo.domain.memberExpense.repotiroy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;

public interface MemberExpenseRepository extends JpaRepository<MemberExpense, Long> {
	List<MemberExpense> findByExpenseId(Long expenseId);
}
