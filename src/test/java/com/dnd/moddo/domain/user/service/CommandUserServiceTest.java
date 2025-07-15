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

import com.dnd.moddo.domain.user.dto.request.GuestUserSaveRequest;
import com.dnd.moddo.domain.user.dto.request.UserSaveRequest;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.service.implementation.UserCreator;
import com.dnd.moddo.domain.user.service.implementation.UserReader;

@ExtendWith(MockitoExtension.class)
public class CommandUserServiceTest {
	@Mock
	private UserCreator userCreator;
	@Mock
	private UserReader userReader;
	@InjectMocks
	private CommandUserService commandUserService;

	@DisplayName("유효한 요청으로 게스트 유저를 생성할 수 있다.")
	@Test
	void whenSaveRequestIsValid_thenGuestUserIsSaved() {
		//given
		GuestUserSaveRequest request = new GuestUserSaveRequest("email", "Guest");
		when(userCreator.createUser(any(User.class))).thenReturn(request.toEntity());
		//when
		User result = commandUserService.createGuestUser(request);
		//then
		assertThat(result.getEmail()).isEqualTo("email");
		assertThat(result.getName()).isEqualTo("Guest");
		assertThat(result.getIsMember()).isFalse();
	}

	@DisplayName("유효한 요청으로 카카오 유저를 생성할 수 있다.")
	@Test
	void whenSaveRequestIsValid_thenKakaoUserIsSaved() {
		//given
		UserSaveRequest request = new UserSaveRequest("email", "Kakao", 123456L);
		when(userCreator.createUser(any(User.class))).thenReturn(request.toEntity());
		//when
		User result = commandUserService.createKakaoUser(request);
		//then
		assertThat(result.getEmail()).isEqualTo("email");
		assertThat(result.getName()).isEqualTo("Kakao");
		assertThat(result.getKakaoId()).isEqualTo(123456L);
	}

	@DisplayName("카카오 ID로 조회 시 유저가 없으면 새로 생성한다")
	@Test
	void whenUserDoesNotExist_thenCreateNewUser() {
		//given
		UserSaveRequest request = new UserSaveRequest("email", "Kakao", 123456L);

		when(userReader.findByKakaoId(anyLong())).thenReturn(Optional.empty());
		when(userCreator.createUser(any(User.class))).thenReturn(request.toEntity());
		//when
		User result = commandUserService.getOrCreateUser(request);

		//then
		assertThat(result.getEmail()).isEqualTo("email");
		assertThat(result.getName()).isEqualTo("Kakao");
		assertThat(result.getKakaoId()).isEqualTo(123456L);
	}

	@DisplayName("카카오 ID로 유저 조회 시 이미 존재하면 기존 유저를 반환한다")
	@Test
	void getOrCreateUser() {
		//given
		UserSaveRequest request = new UserSaveRequest("email", "Kakao", 123456L);

		when(userReader.findByKakaoId(anyLong())).thenReturn(Optional.of(request.toEntity()));
		//when
		User result = commandUserService.getOrCreateUser(request);

		//then
		assertThat(result.getEmail()).isEqualTo("email");
		assertThat(result.getName()).isEqualTo("Kakao");
		assertThat(result.getKakaoId()).isEqualTo(123456L);
	}
}
