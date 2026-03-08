package com.dnd.moddo.domain.user.service.implementation;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.user.application.impl.UserDeleter;
import com.dnd.moddo.user.domain.User;
import com.dnd.moddo.user.infrastructure.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserDeleterTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserDeleter userDeleter;

	@DisplayName("사용자를 성공적으로 삭제(Soft Delete) 처리한다.")
	@Test
	void deleteSuccess() {
		// given
		Long userId = 1L;
		User user = mock(User.class);
		when(userRepository.getById(userId)).thenReturn(user);

		// when
		userDeleter.deleteUser(userId);

		// then
		verify(userRepository, times(1)).getById(userId);
		verify(userRepository, times(1)).delete(user);
	}
}
