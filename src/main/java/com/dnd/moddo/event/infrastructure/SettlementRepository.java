package com.dnd.moddo.event.infrastructure;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.domain.settlement.exception.GroupNotFoundException;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
	default Settlement getById(Long groupId) {
		return findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
	}

	boolean existsByCode(String code);

	@Query("SELECT s.id FROM Settlement s WHERE s.code = :code")
	Optional<Long> findIdByCode(@Param("code") String code);

	@Query("SELECT COUNT(s) FROM Settlement s WHERE s.createdAt BETWEEN :start AND :end")
	long countCreatedSettlement(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("""
		SELECT YEAR(s.createdAt), MONTH(s.createdAt), COUNT(s)
		FROM Settlement s
		WHERE s.createdAt >= :start
		GROUP BY YEAR(s.createdAt), MONTH(s.createdAt)
		ORDER BY YEAR(s.createdAt), MONTH(s.createdAt)
		""")
	List<Object[]> countMonthlySettlements(@Param("start") LocalDateTime start);

	@Query("""
		SELECT HOUR(s.createdAt), COUNT(s)
		FROM Settlement s
		GROUP BY HOUR(s.createdAt)
		ORDER BY HOUR(s.createdAt)
		""")
	List<Object[]> countHourlySettlements();

	List<Settlement> findByCompletedAtIsNotNull();

	@Query("""
		SELECT s.writer
		FROM Settlement s
		GROUP BY s.writer
		HAVING COUNT(s) > 1
		""")
	List<Long> findRepeatWriters();

	@Query("SELECT COUNT(s) FROM Settlement s WHERE s.completedAt BETWEEN :start AND :end")
	long countCompletedSettlement(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("""
		    SELECT COUNT(s)
		    FROM Settlement s
		    WHERE s.completedAt IS NULL
		      AND s.createdAt < :limit
		""")
	long countOverdue(@Param("limit") LocalDateTime limit);

	@Modifying
	@Query("""
			update Settlement s
			   set s.completedAt = CURRENT_TIMESTAMP
			 where s.id = :settlementId
			   and s.completedAt is null
		""")
	int markCompletedIfNotCompleted(@Param("settlementId") Long settlementId);

	default Long getIdByCode(String code) {
		return findIdByCode(code).orElseThrow(() -> new GroupNotFoundException(code));
	}

}
