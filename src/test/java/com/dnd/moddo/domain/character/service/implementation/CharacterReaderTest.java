package com.dnd.moddo.character.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.character.entity.Character;
import com.dnd.moddo.domain.character.repository.CharacterRepository;
import com.dnd.moddo.domain.character.service.implementation.CharacterReader;
import com.dnd.moddo.domain.image.dto.CharacterResponse;
import com.dnd.moddo.domain.image.exception.CharacterNotFoundException;
import com.dnd.moddo.event.domain.settlement.Settlement;

@ExtendWith(MockitoExtension.class)
class CharacterReaderTest {

	@Mock
	private CharacterRepository characterRepository;

	@InjectMocks
	private CharacterReader characterReader;

	private Long groupId;
	private Character mockCharacter;

	@BeforeEach
	void setUp() {
		groupId = 1L;
		Settlement mockSettlement = Settlement.builder()
			.writer(1L)
			.name("Test Group")
			.password("testPassword")
			.build();

		mockCharacter = Character.builder()
			.settlement(mockSettlement)
			.name("러키 모또")
			.rarity("1")
			.imageUrl("https://moddo-s3.s3.amazonaws.com/character/lucky-1.png")
			.imageBigUrl("https://moddo-s3.s3.amazonaws.com/character/lucky-1-big.png")
			.build();
	}

	@DisplayName("존재하는 groupId에 대한 캐릭터를 조회하면 성공한다.")
	@Test
	void getCharacterByGroupIdSuccess() {
		// given
		when(characterRepository.findBySettlementId(groupId)).thenReturn(Optional.of(mockCharacter));

		// when
		CharacterResponse response = characterReader.getCharacterByGroupId(groupId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo(mockCharacter.getName());
		assertThat(response.rarity()).isEqualTo(mockCharacter.getRarity());
		assertThat(response.imageUrl()).isEqualTo(mockCharacter.getImageUrl());
		assertThat(response.imageBigUrl()).isEqualTo(mockCharacter.getImageBigUrl());

		verify(characterRepository, times(1)).findBySettlementId(groupId);
	}

	@DisplayName("존재하지 않는 groupId로 캐릭터를 조회하면 예외가 발생한다.")
	@Test
	void getCharacterByGroupIdNotFound() {
		// given
		when(characterRepository.findBySettlementId(groupId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(CharacterNotFoundException.class, () -> characterReader.getCharacterByGroupId(groupId));

		verify(characterRepository, times(1)).findBySettlementId(groupId);
	}
}
