package com.dnd.moddo.domain.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.user.application.QueryUserService;
import com.dnd.moddo.user.application.impl.UserReader;
import com.dnd.moddo.user.presentation.response.UserResponse;

@ExtendWith(MockitoExtension.class)
public class QueryUserServiceTest {
	@Mock
	private UserReader userReader;
	@InjectMocks
	private QueryUserService queryUserService;

	@DisplayName("userId로 kakaoId를 조회하면 해당 kakaoId를 반환한다")
	@Test
	void whenFindKakaoIdById_thenReturnKakaoId() {
		//given
		Long userId = 1L;
		Long kakaoId = 123456L;
		when(userReader.findKakaoIdById(any())).thenReturn(Optional.of(kakaoId));
		//when
		Optional<Long> result = queryUserService.findKakaoIdById(userId);
		//then
		assertThat(result).isPresent();
		assertThat(result.get()).isEqualTo(kakaoId);
		verify(userReader, times(1)).findKakaoIdById(userId);
	}

	@DisplayName("userId가 없을 때 null을 반환한다")
	@Test
	void whenUserIdNotFound_thenReturnNull() {
		//given
		when(userReader.findKakaoIdById(any())).thenReturn(Optional.empty());

		//when
		Optional<Long> result = queryUserService.findKakaoIdById(1L);

		//then
		assertThat(result).isEmpty();
		verify(userReader, times(1)).findKakaoIdById(1L);
	}

	@DisplayName("userId로 UserResponse를 조회하면 해당 UserResponse를 반환한다")
	@Test
	void whenFindUserById_thenReturnUserResponse() {
		//given
		Long userId = 1L;
		UserResponse userResponse = UserResponse.builder()
			.name("연노른자")
			.email("test@example.com")
			.profile("profile.png")
			.build();
		when(userReader.findById(userId)).thenReturn(userResponse);

		//when
		UserResponse result = queryUserService.findUserById(userId);

		//then
		assertThat(result).isEqualTo(userResponse);
		verify(userReader, times(1)).findById(userId);
	}
}
