package com.dnd.moddo.domain.settlement.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.settlement.exception.GroupNotFoundException;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
	default Settlement getById(Long groupId) {
		return findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
	}

	boolean existsByCode(String code);

	@Query("SELECT s.id FROM Settlement s WHERE s.code = :code")
	Optional<Long> findIdByCode(@Param("code") String code);

	@Query("SELECT COUNT(s) FROM Settlement s WHERE s.createdAt BETWEEN :start AND :end")
	long countCreatedSettlement(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("SELECT COUNT(s) FROM Settlement s WHERE s.completedAt BETWEEN :start AND :end")
	long countCompletedSettlement(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("""
		    SELECT COUNT(s)
		    FROM Settlement s
		    WHERE s.completedAt IS NULL
		      AND s.createdAt < :limit
		""")
	long countOverdue(@Param("limit") LocalDateTime limit);

	default Long getIdByCode(String code) {
		return findIdByCode(code).orElseThrow(() -> new GroupNotFoundException(code));
	}

}
