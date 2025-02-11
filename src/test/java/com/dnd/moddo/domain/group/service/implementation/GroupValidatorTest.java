package com.dnd.moddo.domain.group.service.implementation;

import com.dnd.moddo.domain.group.exception.GroupNotAuthorException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GroupValidatorTest {

    private final GroupValidator groupValidator = new GroupValidator();

    @Test
    @DisplayName("그룹 작성자와 요청 사용자가 같으면 예외가 발생하지 않는다.")
    void checkGroupAuthor_Success() {
        // Given
        Long groupWriter = 1L;
        Long writer = 1L;

        // When & Then
        groupValidator.checkGroupAuthor(groupWriter, writer);
    }

    @Test
    @DisplayName("그룹 작성자와 요청 사용자가 다르면 GroupNotAuthorException 예외가 발생한다.")
    void checkGroupAuthor_Fail() {
        // Given
        Long groupWriter = 1L;
        Long writer = 2L;

        // When & Then
        assertThatThrownBy(() -> groupValidator.checkGroupAuthor(groupWriter, writer))
                .isInstanceOf(GroupNotAuthorException.class);
    }
}
