package com.dnd.moddo.domain.group.entity;

import com.dnd.moddo.domain.group.dto.request.GroupAccountRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long writer;

    private String name;

    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime expiredAt;

    private String bank;

    private String accountNumber;

    private LocalDateTime deadline;

    @Builder
    public Group(String name, Long writer, String password, LocalDateTime createdAt, LocalDateTime expiredAt, String bank, String accountNumber, LocalDateTime deadline) {
        this.name = name;
        this.writer = writer;
        this.password = password;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.deadline = LocalDateTime.now().plusDays(1);
    }

    public void updateAccount(GroupAccountRequest request) {
        this.bank = request.bank();
        this.accountNumber = request.accountNumber();
    }

    public boolean isWriter(Long userId) {
        return this.writer.equals(userId);
    }
}
