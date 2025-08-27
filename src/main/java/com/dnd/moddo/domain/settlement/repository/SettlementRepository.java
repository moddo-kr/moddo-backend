package com.dnd.moddo.domain.settlement.repository;

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

	@Query("SELECT g.id FROM Settlement g WHERE g.code = :code")
	Optional<Long> findIdByCode(@Param("code") String code);

	default Long getIdByCode(String code) {
		return findIdByCode(code).orElseThrow(() -> new GroupNotFoundException(code));
	}

}
