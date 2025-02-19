package com.dnd.moddo.domain.expense.repository;

import java.util.List;

import com.dnd.moddo.domain.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.exception.ExpenseNotFoundException;
import org.springframework.data.jpa.repository.Query;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	List<Expense> findByGroupId(Long groupId);

	List<Expense> findByGroupIdOrderByDateAsc(Long id);

	@Query("SELECT SUM(e.amount) FROM Expense e WHERE e.group = :group")
	Long sumAmountByGroup(Group group);
	
	default Expense getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new ExpenseNotFoundException(id));
	}
}
