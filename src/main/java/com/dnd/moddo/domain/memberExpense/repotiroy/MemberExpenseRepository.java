package com.dnd.moddo.domain.memberExpense.repotiroy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;

public interface MemberExpenseRepository extends JpaRepository<MemberExpense, Long> {
	List<MemberExpense> findByExpenseId(Long expenseId);

	@Query("select me from MemberExpense me where me.groupMember.id in :groupMemberIds")
	List<MemberExpense> findAllByGroupMemberIds(@Param("groupMemberIds") List<Long> groupMemberIds);
}
