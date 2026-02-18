package com.dnd.moddo.domain.user.entity;

import static com.dnd.moddo.global.support.UserTestFactory.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dnd.moddo.user.domain.User;
import com.dnd.moddo.user.domain.exception.UserNotFoundException;
import com.dnd.moddo.user.infrastructure.UserRepository;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserTest {

	@Autowired
	private UserRepository userRepository;

	@DisplayName("이메일로 사용자를 조회할 수 있다.")
	@Test
	public void findByUser() {
		// Given
		LocalDateTime time = LocalDateTime.now();

		User user1 = createGuestWithNameAndEmail("홍길동", "guest-UUID1@guest.com");
		User user2 = createGuestWithNameAndEmail("심청이", "guest-UUID2@guest.com");

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
