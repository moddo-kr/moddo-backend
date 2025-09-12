package com.dnd.moddo.domain.appointmentMember.service.implementation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.appointmentMember.exception.AppointmentMemberDuplicateNameException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AppointmentMemberValidator {

	public void validateMemberNamesNotDuplicate(List<String> names) {
		if (hasDuplicateMemberName(names)) {
			throw new AppointmentMemberDuplicateNameException();
		}
	}

	private boolean hasDuplicateMemberName(List<String> names) {
		Set<String> uniqueNames = new HashSet<>(names);
		return uniqueNames.size() != names.size();
	}

}
