package com.dnd.moddo.domain.expense.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.exception.ExpenseNotFoundException;
import com.dnd.moddo.domain.settlement.entity.Settlement;

import feign.Param;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	List<Expense> findBySettlementId(Long settlementId);

	List<Expense> findBySettlementIdOrderByDateAsc(Long id);

	@Query("SELECT SUM(e.amount) FROM Expense e WHERE e.settlement = :settlement")
	Long sumAmountBySettlement(Settlement settlement);

	@Query("""
		    SELECT 
		        SUM(e.amount),
		        AVG(e.amount),
		        MAX(e.amount),
		        MIN(e.amount)
		    FROM Expense e
		    JOIN e.settlement s
		    WHERE s.createdAt BETWEEN :start AND :end
		""")
	Object[] amountStats(
		@Param("start") LocalDateTime start,
		@Param("end") LocalDateTime end
	);

	default Expense getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new ExpenseNotFoundException(id));
	}
}
