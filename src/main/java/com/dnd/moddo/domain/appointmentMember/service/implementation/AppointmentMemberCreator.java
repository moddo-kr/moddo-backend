package com.dnd.moddo.domain.appointmentMember.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.appointmentMember.repository.AppointmentMemberRepository;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class AppointmentMemberCreator {
	private final AppointmentMemberRepository appointmentMemberRepository;
	private final UserRepository userRepository;

	public AppointmentMember createManagerForGroup(Settlement settlement, Long userId) {
		User user = userRepository.getById(userId);

		String name = user.getIsMember() ? user.getName() : "김모또";

		AppointmentMember appointmentMember = AppointmentMember.builder()
			.name(name)
			.settlement(settlement)
			.profileId(null)
			.profileId(0)
			.role(ExpenseRole.MANAGER)
			.build();

		appointmentMember.updatePaymentStatus(true);

		return appointmentMemberRepository.save(appointmentMember);
	}
}
