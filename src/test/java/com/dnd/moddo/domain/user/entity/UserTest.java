package com.dnd.moddo.domain.user.entity;

import com.dnd.moddo.ModdoApplication;
import com.dnd.moddo.domain.user.exception.UserNotFoundException;
import com.dnd.moddo.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static com.dnd.moddo.domain.user.entity.type.Authority.USER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ModdoApplication.class)
@SpringBootTest
public class UserTest {

    @Autowired
    private UserRepository userRepository;

    @DisplayName("이메일로 사용자를 조회할 수 있다.")
    @Test
    public void findByUser() {
        // Given
        LocalDateTime time = LocalDateTime.now();

        User user1 = new User("홍길동", "guest-UUID1@guest.com", "profile.png", false, USER, time, time.plusDays(7));
        User user2 = new User("심청이", "guest-UUID2@guest.com", "profile.png", false, USER, time, time.plusDays(7));

        userRepository.save(user1);
        userRepository.save(user2);

        // When
        User foundUser = userRepository.getByEmail("guest-UUID2@guest.com");

        // Then
        assertThat(foundUser.getName()).isEqualTo("심청이");
        assertThat(foundUser.getEmail()).isEqualTo("guest-UUID2@guest.com");
    }


    @DisplayName("이메일로 사용자를 조회할 때, 사용자가 없으면 예외를 발생시킨다.")
    @Test
    public void getByEmailNotFound() {
        // When & Then
        assertThrows(UserNotFoundException.class, () -> userRepository.getByEmail("exception@guest.com"));
    }

}
