package com.dnd.moddo.domain.appointmentMember.entity.type;

import com.dnd.moddo.domain.appointmentMember.exception.ExpenseRoleNotFoundException;

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
