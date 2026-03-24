package com.dnd.moddo.domain.reward.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.dnd.moddo.reward.application.RewardService;
import com.dnd.moddo.reward.domain.character.Character;
import com.dnd.moddo.reward.domain.character.exception.SettlementCharacterNotFoundException;
import com.dnd.moddo.reward.infrastructure.CollectionRepository;
import com.dnd.moddo.reward.infrastructure.RewardQueryRepository;

@ExtendWith(MockitoExtension.class)
class RewardServiceGrantTest {

	@Mock
	private RewardQueryRepository rewardQueryRepository;

	@Mock
	private CollectionRepository collectionRepository;

	@InjectMocks
	private RewardService rewardService;

	@Test
	@DisplayName("정산에 연결된 캐릭터가 없으면 예외가 발생한다.")
	void throwExceptionWhenSettlementCharacterNotFound() {
		when(rewardQueryRepository.findBySettlementId(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> rewardService.grant(1L, 2L))
			.isInstanceOf(SettlementCharacterNotFoundException.class);
	}

	@Test
	@DisplayName("이미 같은 캐릭터를 보유 중이면 저장하지 않는다.")
	void doNotSaveWhenCharacterAlreadyGranted() {
		Character character = Character.builder()
			.name("모또")
			.rarity(1)
			.imageUrl("image")
			.imageBigUrl("image-big")
			.build();
		setCharacterId(character, 3L);

		when(rewardQueryRepository.findBySettlementId(1L)).thenReturn(Optional.of(character));
		when(collectionRepository.existsByUserIdAndCharacterId(2L, 3L)).thenReturn(true);

		rewardService.grant(1L, 2L);

		verify(collectionRepository, never()).save(any());
	}

	@Test
	@DisplayName("중복 지급으로 인한 제약조건 예외는 멱등 성공으로 처리한다.")
	void swallowDuplicateGrantException() {
		Character character = Character.builder()
			.name("모또")
			.rarity(1)
			.imageUrl("image")
			.imageBigUrl("image-big")
			.build();
		setCharacterId(character, 3L);

		when(rewardQueryRepository.findBySettlementId(1L)).thenReturn(Optional.of(character));
		when(collectionRepository.existsByUserIdAndCharacterId(2L, 3L)).thenReturn(false);
		doThrow(new DataIntegrityViolationException("duplicate")).when(collectionRepository).save(any());

		assertThatCode(() -> rewardService.grant(1L, 2L))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("보상을 지급할 수 있으면 컬렉션을 저장한다.")
	void saveCollectionWhenGrantAvailable() {
		Character character = Character.builder()
			.name("모또")
			.rarity(1)
			.imageUrl("image")
			.imageBigUrl("image-big")
			.build();
		setCharacterId(character, 3L);

		when(rewardQueryRepository.findBySettlementId(1L)).thenReturn(Optional.of(character));
		when(collectionRepository.existsByUserIdAndCharacterId(2L, 3L)).thenReturn(false);

		rewardService.grant(1L, 2L);

		verify(collectionRepository).save(any());
	}

	private void setCharacterId(Character character, Long id) {
		try {
			java.lang.reflect.Field idField = Character.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(character, id);
		} catch (ReflectiveOperationException exception) {
			throw new RuntimeException(exception);
		}
	}
}
