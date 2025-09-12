package com.dnd.moddo.domain.memberExpense.repotiroy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;

public interface MemberExpenseRepository extends JpaRepository<MemberExpense, Long> {
	//@EntityGraph(attributePaths = {"groupMember"})
	List<MemberExpense> findByExpenseId(Long expenseId);

	@Query("select me from MemberExpense me where me.expenseId in :expenseIds")
	List<MemberExpense> findAllByExpenseIds(@Param("expenseIds") List<Long> expenseIds);

	@Query("select me from MemberExpense me where me.appointmentMember.id in :appointmentMemberIds")
	List<MemberExpense> findAllByAppointmentMemberIds(@Param("appointmentMemberIds") List<Long> appointmentMemberIds);
}
