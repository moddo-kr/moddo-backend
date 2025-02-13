package com.dnd.moddo.domain.group.service.implementation;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.exception.GroupNotAuthorException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GroupValidatorTest {

    private final GroupValidator groupValidator = new GroupValidator();

    @Test
    @DisplayName("그룹 작성자와 요청 사용자가 같으면 예외가 발생하지 않는다.")
    void checkGroupAuthor_Success() {
        // Given
        Group group = mock(Group.class);
        Long writer = 1L;
        when(group.isWriter(writer)).thenReturn(true);

        // When & Then
        groupValidator.checkGroupAuthor(group, writer);
    }

    @Test
    @DisplayName("그룹 작성자와 요청 사용자가 다르면 GroupNotAuthorException 예외가 발생한다.")
    void checkGroupAuthor_Fail() {
        Group group = mock(Group.class);
        Long writer = 1L;
        when(group.isWriter(writer)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> groupValidator.checkGroupAuthor(group, writer))
                .isInstanceOf(GroupNotAuthorException.class);
    }
}
