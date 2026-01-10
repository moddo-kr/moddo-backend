package com.dnd.moddo.event.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.member.exception.MemberNotFoundException;

public interface MemberRepository extends JpaRepository<Member, Long> {

	@Query("select gm from Member gm where gm.settlement.id = :settlementId order by "
		+ "case when gm.role = 'MANAGER' then 1 else 2 end, "
		+ "case when gm.paidAt is null then 1 else 0 end, "
		+ "gm.paidAt asc, "
		+ "gm.name asc")
	List<Member> findBySettlementId(@Param("settlementId") Long settlementId);

	@Query("select gm.id from Member gm where gm.settlement.id = :settlementId")
	List<Long> findAppointmentMemberIdsBySettlementId(@Param("settlementId") Long settlementId);

	default Member getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new MemberNotFoundException(id));
	}
}
