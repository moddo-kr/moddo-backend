package com.dnd.moddo.domain.settlement.service.implementation;

import static com.dnd.moddo.global.support.UserTestFactory.*;
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

import com.dnd.moddo.event.application.impl.SettlementCreator;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.infrastructure.SettlementRepository;
import com.dnd.moddo.event.presentation.request.SettlementRequest;
import com.dnd.moddo.image.application.impl.ImageReader;
import com.dnd.moddo.image.presentation.response.CharacterResponse;
import com.dnd.moddo.reward.domain.character.Character;
import com.dnd.moddo.reward.infrastructure.CharacterRepository;
import com.dnd.moddo.user.domain.User;
import com.dnd.moddo.user.infrastructure.UserRepository;

@ExtendWith(MockitoExtension.class)
class SettlementCreatorTest {
	@Mock
	private SettlementRepository settlementRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CharacterRepository characterRepository;

	@Mock
	private ImageReader imageReader;

	@InjectMocks
	private SettlementCreator settlementCreator;

	private Long userId;
	private SettlementRequest request;
	private User mockUser;
	private Settlement mockSettlement;
	private CharacterResponse mockCharacterResponse;

	@BeforeEach
	void setUp() {
		userId = 1L;
		request = new SettlementRequest("groupName");

		mockUser = createGuestDefault();

		mockSettlement = Settlement.builder()
			.writer(userId)
			.name(request.name())
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
	void createSettlementSuccess() {
		// given
		when(userRepository.getById(userId)).thenReturn(mockUser);
		when(settlementRepository.save(any(Settlement.class))).thenReturn(mockSettlement);
		when(imageReader.getRandomCharacter()).thenReturn(mockCharacterResponse);
		when(characterRepository.save(any(Character.class))).thenAnswer(invocation -> invocation.getArgument(0));

		when(settlementRepository.existsByCode(anyString()))
			.thenReturn(false);

		// when
		Settlement response = settlementCreator.createSettlement(request, userId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getName()).isEqualTo(request.name());

		verify(userRepository, times(1)).getById(userId);
		verify(settlementRepository, times(1)).save(any(Settlement.class));
		verify(imageReader, times(1)).getRandomCharacter();
		verify(characterRepository, times(1)).save(any(Character.class));
		verify(settlementRepository, times(1)).existsByCode(anyString());
	}

	@DisplayName("모임 생성 시 중복된 group code를 5번 생성시 예외가 발생한다. ")
	@Test
	void whenGroupCodeIsDuplicatedFiveTimes_thenThrowsException() {
		// given
		when(userRepository.getById(userId)).thenReturn(mockUser);
		when(settlementRepository.existsByCode(anyString())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> settlementCreator.createSettlement(request, userId))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("코드");

		verify(userRepository, times(1)).getById(anyLong());
		verify(settlementRepository, times((5))).existsByCode(anyString());
	}
}
