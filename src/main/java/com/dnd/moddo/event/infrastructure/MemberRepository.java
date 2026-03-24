package com.dnd.moddo.event.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.member.exception.MemberNotFoundException;

public interface MemberRepository extends JpaRepository<Member, Long> {

	@Query("select gm.id from Member gm where gm.settlement.id = :settlementId")
	List<Long> findMemberIdsBySettlementId(@Param("settlementId") Long settlementId);

	@Query("""
			select count(gm) > 0
			from Member gm
			where gm.settlement.id = :settlementId
			  and gm.user.id = :userId
		""")
	boolean existsBySettlementIdAndUserId(@Param("settlementId") Long settlementId, @Param("userId") Long userId);

	@Query("""
			select gm
			from Member gm
			where gm.settlement.id = :settlementId
			  and gm.user.id = :userId
		""")
	Optional<Member> findBySettlementIdAndUserId(@Param("settlementId") Long settlementId, @Param("userId") Long userId);

	@Query("""
			select count(gm) > 0
			from Member gm
			where gm.settlement.id = :settlementId
			  and gm.isPaid = false
		""")
	boolean existsBySettlementIdAndIsPaidFalse(@Param("settlementId") Long settlementId);

	default Member getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new MemberNotFoundException(id));
	}
}
