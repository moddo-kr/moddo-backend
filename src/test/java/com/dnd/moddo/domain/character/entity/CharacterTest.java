package com.dnd.moddo.domain.character.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.reward.domain.character.Character;

class CharacterTest {

	@DisplayName("Character 엔티티가 정상적으로 생성된다.")
	@Test
	void characterEntityCreationTest() {
		// given
		Settlement settlement = Settlement.builder()
			.writer(1L)
			.name("Test Group")
			.createdAt(LocalDateTime.now())
			.build();

		// when
		Character character = Character.builder()
			.name("러키 모또")
			.rarity(1)
			.imageUrl("https://moddo-s3.s3.amazonaws.com/character/lucky-1.png")
			.imageBigUrl("https://moddo-s3.s3.amazonaws.com/character/lucky-1-big.png")
			.build();

		// then
		assertThat(character).isNotNull();
		assertThat(character.getName()).isEqualTo("러키 모또");
		assertThat(character.getRarity()).isEqualTo(1L);
		assertThat(character.getImageUrl()).isEqualTo("https://moddo-s3.s3.amazonaws.com/character/lucky-1.png");
		assertThat(character.getImageBigUrl()).isEqualTo("https://moddo-s3.s3.amazonaws.com/character/lucky-1-big.png");
	}
}
