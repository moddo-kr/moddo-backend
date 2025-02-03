package com.dnd.moddo.domain.group.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class GroupTest {

    @Test
    void groupCreateTest() {
        // given
        String name = "groupName";
        Long writer = 1L;
        String password = "password";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiredAt = createdAt.plusDays(1);
        String bank = "bank";
        String accountNumber = "1234-1234";

        // when
        Group group = Group.builder()
                .name(name)
                .writer(writer)
                .password(password)
                .createdAt(createdAt)
                .expiredAt(expiredAt)
                .bank(bank)
                .accountNumber(accountNumber)
                .build();

        // then
        assertThat(group).isNotNull();
        assertThat(group.getName()).isEqualTo(name);
        assertThat(group.getWriter()).isEqualTo(writer);
        assertThat(group.getPassword()).isEqualTo(password);
        assertThat(group.getCreatedAt()).isEqualTo(createdAt);
        assertThat(group.getExpiredAt()).isEqualTo(expiredAt);
        assertThat(group.getBank()).isEqualTo(bank);
        assertThat(group.getAccountNumber()).isEqualTo(accountNumber);
    }
}