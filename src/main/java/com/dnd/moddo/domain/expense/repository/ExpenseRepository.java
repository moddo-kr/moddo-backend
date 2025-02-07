package com.dnd.moddo.domain.expense.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.exception.ExpenseNotFoundException;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	List<Expense> findByGroupId(Long groupId);

	List<Expense> findByGroupIdOrderByOrderAsc(Long id);

	@Query("select COALESCE(MAX(e.order),0) from Expense e where e.group.id = :groupId")
	int findMaxOrderByGroupId(@Param("groupId") Long groupId);

	default Expense getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new ExpenseNotFoundException(id));
	}

}
