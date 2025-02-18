package com.dnd.moddo.domain.group.service.implementation;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.repository.ExpenseRepository;
import com.dnd.moddo.domain.group.dto.response.GroupHeaderResponse;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupReader {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ExpenseRepository expenseRepository;

    public Group read(Long groupId) {
        return groupRepository.getById(groupId);
    }

    public List<GroupMember> findByGroup(Long groupId) {
        return groupMemberRepository.findByGroupId(groupId);
    }

    public GroupHeaderResponse findByHeader(Long groupId) {
        Group group = groupRepository.getById(groupId);
        Long totalAmount = expenseRepository.sumAmountByGroup(group);

        return GroupHeaderResponse.of(group.getName(), totalAmount, group.getDeadline(), group.getBank(), group.getAccountNumber());
    }
}
