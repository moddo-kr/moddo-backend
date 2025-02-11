package com.dnd.moddo.domain.groupMember.entity.type;

import com.dnd.moddo.domain.groupMember.exception.ExpenseRoleNotFoundException;

public enum ExpenseRole {
	MANAGER, PARTICIPANT;

	public static ExpenseRole getRoleByString(String role) {
		try {
			return ExpenseRole.valueOf(role);
		} catch (IllegalArgumentException e) {
			throw new ExpenseRoleNotFoundException(role);
		}
	}
}
