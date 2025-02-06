package com.dnd.moddo.domain.groupMember.service.implementation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.groupMember.exception.GroupMemberDuplicateNameException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GroupMemberValidator {

	public void validateMemberNamesNotDuplicate(List<String> names) {
		if (hasDuplicateMemberName(names)) {
			throw new GroupMemberDuplicateNameException();
		}
	}

	private boolean hasDuplicateMemberName(List<String> names) {
		Set<String> uniqueNames = new HashSet<>(names);
		return uniqueNames.size() != names.size();
	}
}
