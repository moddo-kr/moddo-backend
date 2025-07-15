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

import com.dnd.moddo.domain.user.service.implementation.UserReader;

@ExtendWith(MockitoExtension.class)
public class queryUserServiceTest {
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
		Long result = queryUserService.findKakaoIdById(userId);
		//then
		assertThat(result).isEqualTo(kakaoId);
		verify(userReader, times(1)).findKakaoIdById(userId);
	}

	@DisplayName("userId가 없을 때 null을 반환한다")
	@Test
	void whenUserIdNotFound_thenReturnNull() {
		//given
		when(userReader.findKakaoIdById(any())).thenReturn(Optional.empty());

		//when
		Long result = queryUserService.findKakaoIdById(1L);

		//then
		assertThat(result).isNull();
		verify(userReader, times(1)).findKakaoIdById(1L);
	}
}
