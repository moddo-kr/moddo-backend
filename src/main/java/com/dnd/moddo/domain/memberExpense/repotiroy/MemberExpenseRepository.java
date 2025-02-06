package com.dnd.moddo.domain.memberExpense.repotiroy;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;

public interface MemberExpenseRepository extends JpaRepository<MemberExpense, Long> {
}
