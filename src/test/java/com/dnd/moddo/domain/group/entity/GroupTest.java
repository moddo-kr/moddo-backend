package com.dnd.moddo.domain.group.entity;

import com.dnd.moddo.domain.group.repository.GroupRepository;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class GroupTest {

    @Autowired
    private GroupRepository groupRepository;

    @Test
    void groupCreateTest() {
        // given
        Group group1 = new Group("groupName", 1L, "password", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "bank", "1234-1234");
        Group group2 = new Group("groupName", 1L, "password", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "bank", "1234-1234");
        groupRepository.save(group1);
        groupRepository.save(group2);

        // when
        Optional<Group> foundGroup = groupRepository.findById(1L);

        // then
        assertThat(foundGroup).isPresent();
        assertThat(foundGroup.get().getName()).isEqualTo("groupName");
        assertThat(foundGroup.get().getWriter()).isEqualTo(1L);
    }
}