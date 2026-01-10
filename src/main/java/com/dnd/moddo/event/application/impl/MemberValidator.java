package com.dnd.moddo.event.application.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.dnd.moddo.event.domain.member.exception.MemberDuplicateNameException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberValidator {

	public void validateMemberNamesNotDuplicate(List<String> names) {
		if (hasDuplicateMemberName(names)) {
			throw new MemberDuplicateNameException();
		}
	}

	private boolean hasDuplicateMemberName(List<String> names) {
		Set<String> uniqueNames = new HashSet<>(names);
		return uniqueNames.size() != names.size();
	}

}
