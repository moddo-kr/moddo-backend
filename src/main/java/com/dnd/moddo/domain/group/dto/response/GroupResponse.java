package com.dnd.moddo.domain.group.dto.response;

import java.time.LocalDateTime;

import com.dnd.moddo.domain.group.entity.Group;

public record GroupResponse(
	Long id,
	Long writer,
	LocalDateTime createdAt,
	LocalDateTime expiredAt,
	String bank,
	String accountNumber,
	LocalDateTime deadline
) {
	public static GroupResponse of(Group group) {
		return new GroupResponse(
			group.getId(),
			group.getWriter(),
			group.getCreatedAt(),
			group.getExpiredAt(),
			group.getBank(),
			group.getAccountNumber(),
			group.getDeadline()
		);
	}
}
