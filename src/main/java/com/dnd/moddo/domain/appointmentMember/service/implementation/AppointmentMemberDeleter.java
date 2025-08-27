package com.dnd.moddo.domain.appointmentMember.service.implementation;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.exception.ManagerCannotDeleteException;
import com.dnd.moddo.domain.appointmentMember.repository.AppointmentMemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentMemberDeleter {
	private final AppointmentMemberRepository appointmentMemberRepository;
	private final AppointmentMemberReader appointmentMemberReader;

	public void delete(Long appointmentMemberId) {
		AppointmentMember appointmentMember = appointmentMemberReader.findByAppointmentMemberId(appointmentMemberId);
		if (appointmentMember.isManager()) {
			throw new ManagerCannotDeleteException(appointmentMemberId);
		}
		appointmentMemberRepository.delete(appointmentMember);
	}
}
