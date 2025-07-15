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

import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;

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

		when(userRepository.findByKakaoId(any())).thenReturn(Optional.of(user));
		//when
		Optional<User> result = userReader.findByKakaoId(kakaoId);
		//then
		assertThat(result).isPresent();
		assertThat(result.get().getKakaoId()).isEqualTo(kakaoId);
		verify(userRepository, times(1)).findByKakaoId(kakaoId);
	}
}
