package com.dnd.moddo.domain.group.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.dnd.moddo.domain.character.entity.Character;
import com.dnd.moddo.domain.character.repository.CharacterRepository;
import com.dnd.moddo.domain.group.dto.request.GroupRequest;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import com.dnd.moddo.domain.image.dto.CharacterResponse;
import com.dnd.moddo.domain.image.service.implementation.ImageReader;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.entity.type.Authority;
import com.dnd.moddo.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class GroupCreatorTest {
	@Mock
	private GroupRepository groupRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CharacterRepository characterRepository;

	@Mock
	private ImageReader imageReader;

	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	private GroupCreator groupCreator;

	private Long userId;
	private GroupRequest request;
	private User mockUser;
	private String encodedPassword;
	private Group mockGroup;
	private CharacterResponse mockCharacterResponse;

	@BeforeEach
	void setUp() {
		userId = 1L;
		request = new GroupRequest("groupName", "password");

		mockUser = new User(userId, "test@example.com", "닉네임", "프로필", false, LocalDateTime.now(),
			LocalDateTime.now().plusDays(1), Authority.USER);

		encodedPassword = "encryptedPassword";

		mockGroup = Group.builder()
			.writer(userId)
			.name(request.name())
			.password(encodedPassword)
			.createdAt(LocalDateTime.now())
			.build();

		mockCharacterResponse = new CharacterResponse(
			"러키 모또",
			"1",
			"https://moddo-s3.s3.amazonaws.com/character/lucky-1.png",
			"https://moddo-s3.s3.amazonaws.com/character/lucky-1-big.png"
		);
	}

	@DisplayName("사용자는 모임 생성 시 랜덤 캐릭터도 함께 저장된다.")
	@Test
	void createGroupSuccess() {
		// given
		when(userRepository.getById(userId)).thenReturn(mockUser);
		when(passwordEncoder.encode(request.password())).thenReturn(encodedPassword);
		when(groupRepository.save(any(Group.class))).thenReturn(mockGroup);
		when(imageReader.getRandomCharacter()).thenReturn(mockCharacterResponse);
		when(characterRepository.save(any(Character.class))).thenAnswer(invocation -> invocation.getArgument(0));

		when(groupRepository.existsByCode(anyString()))
			.thenReturn(false);

		// when
		Group response = groupCreator.createGroup(request, userId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getName()).isEqualTo(request.name());

		verify(userRepository, times(1)).getById(userId);
		verify(passwordEncoder, times(1)).encode(request.password());
		verify(groupRepository, times(1)).save(any(Group.class));
		verify(imageReader, times(1)).getRandomCharacter();
		verify(characterRepository, times(1)).save(any(Character.class));
		verify(groupRepository, times(1)).existsByCode(anyString());
	}

	@DisplayName("모임 생성 시 중복된 group code를 5번 생성시 예외가 발생한다. ")
	@Test
	void whenGroupCodeIsDuplicatedFiveTimes_thenThrowsException() {
		// given
		when(userRepository.getById(userId)).thenReturn(mockUser);
		when(passwordEncoder.encode(request.password())).thenReturn(encodedPassword);

		when(groupRepository.existsByCode(anyString())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> groupCreator.createGroup(request, userId))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("코드");

		verify(userRepository, times(1)).getById(anyLong());
		verify(groupRepository, times((5))).existsByCode(anyString());
	}
}
