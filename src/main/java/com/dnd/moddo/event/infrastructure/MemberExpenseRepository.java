package com.dnd.moddo.event.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.moddo.event.domain.memberExpense.MemberExpense;

public interface MemberExpenseRepository extends JpaRepository<MemberExpense, Long> {
	//@EntityGraph(attributePaths = {"groupMember"})
	List<MemberExpense> findByExpenseId(Long expenseId);

	@Query("select me from MemberExpense me where me.expenseId in :expenseIds")
	List<MemberExpense> findAllByExpenseIds(@Param("expenseIds") List<Long> expenseIds);

	@Query("select me from MemberExpense me where me.member.id in :memberIds")
	List<MemberExpense> findAllByAppointmentMemberIds(@Param("memberIds") List<Long> memberIds);
}
