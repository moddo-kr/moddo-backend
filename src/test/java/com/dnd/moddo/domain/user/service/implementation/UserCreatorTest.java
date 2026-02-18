package com.dnd.moddo.domain.user.service.implementation;

import static com.dnd.moddo.global.support.UserTestFactory.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.user.application.impl.UserCreator;
import com.dnd.moddo.user.domain.User;
import com.dnd.moddo.user.infrastructure.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserCreatorTest {
	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private UserCreator userCreator;

	@Test
	@DisplayName("사용자 생성 시 userRepository.save가 호출되고 저장된 User를 반환한다")
	void whenCreateUser_thenReturnSavedUser() {
		// given
		User user = createWithEmail("test@example.com");

		when(userRepository.save(user)).thenReturn(user);

		// when
		User result = userCreator.createUser(user);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getEmail()).isEqualTo("test@example.com");
		verify(userRepository, times(1)).save(user);
	}
}
