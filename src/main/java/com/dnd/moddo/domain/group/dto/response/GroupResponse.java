package com.dnd.moddo.domain.group.dto.response;

import com.dnd.moddo.domain.group.entity.Group;

import java.time.LocalDateTime;

public record GroupResponse(
        Long id,
        Long writer,
        LocalDateTime createdAt,
        LocalDateTime expiredAt,
        String bank,
        String accountNumber
) {
    public static GroupResponse of(Group group) {
        return new GroupResponse(
                group.getId(),
                group.getWriter(),
                group.getCreatedAt(),
                group.getExpiredAt(),
                group.getBank(),
                group.getAccountNumber()
        );
    }
}
