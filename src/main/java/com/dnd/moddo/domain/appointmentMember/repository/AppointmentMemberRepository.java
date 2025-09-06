package com.dnd.moddo.domain.appointmentMember.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.exception.AppointmentMemberNotFoundException;

public interface AppointmentMemberRepository extends JpaRepository<AppointmentMember, Long> {

	@Query("select gm from AppointmentMember gm where gm.settlement.id = :settlementId order by "
		+ "case when gm.role = 'MANAGER' then 1 else 2 end, "
		+ "case when gm.paidAt is null then 1 else 0 end, "
		+ "gm.paidAt asc, "
		+ "gm.name asc")
	List<AppointmentMember> findBySettlementId(@Param("settlementId") Long settlementId);

	@Query("select gm.id from AppointmentMember gm where gm.settlement.id = :settlementId")
	List<Long> findAppointmentMemberIdsBySettlementId(@Param("settlementId") Long settlementId);

	default AppointmentMember getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new AppointmentMemberNotFoundException(id));
	}
}
