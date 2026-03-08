package com.dnd.moddo.domain.user.service.implementation;

import static com.dnd.moddo.global.support.UserTestFactory.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.user.application.impl.UserReader;
import com.dnd.moddo.user.domain.User;
import com.dnd.moddo.user.infrastructure.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserReaderTest {
	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private UserReader userReader;

	@DisplayName("kakaoId로 User를 조회하면 해당 User를 반환한다")
	@Test
	void whenFindByKakaoId_thenReturnUser() {
		//given
		User user = createWithEmail("test@example.com");
		Long kakaoId = user.getKakaoId();

		when(userRepository.findByKakaoId(kakaoId)).thenReturn(Optional.of(user));
		//when
		Optional<User> result = userReader.findByKakaoId(kakaoId);
		//then
		assertThat(result).isPresent();
		assertThat(result.get().getKakaoId()).isEqualTo(kakaoId);
		verify(userRepository, times(1)).findByKakaoId(kakaoId);
	}

	@DisplayName("userId로 kakaoId를 조회하면 해당 kakaoId를 반환한다")
	@Test
	void whenFindKakaoIdById_thenReturnKakaoId() {
		//given
		Long userId = 1L;
		Long kakaoId = 12345L;
		when(userRepository.findKakaoIdById(userId)).thenReturn(Optional.of(kakaoId));

		//when
		Optional<Long> result = userReader.findKakaoIdById(userId);

		//then
		assertThat(result).isPresent();
		assertThat(result.get()).isEqualTo(kakaoId);
		verify(userRepository, times(1)).findKakaoIdById(userId);
	}

	@DisplayName("userId로 User를 조회하면 UserResponse를 반환한다")
	@Test
	void whenFindById_thenReturnUserResponse() {
		//given
		Long userId = 1L;
		User user = createWithEmail("test@example.com");
		when(userRepository.getById(userId)).thenReturn(user);

		//when
		com.dnd.moddo.user.presentation.response.UserResponse result = userReader.findById(userId);

		//then
		assertThat(result.name()).isEqualTo(user.getName());
		assertThat(result.email()).isEqualTo(user.getEmail());
		assertThat(result.profile()).isEqualTo(user.getProfile());
		verify(userRepository, times(1)).getById(userId);
	}
}
