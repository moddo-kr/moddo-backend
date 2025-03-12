package com.dnd.moddo.character.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.domain.character.entity.Character;
import com.dnd.moddo.domain.group.entity.Group;

class CharacterTest {

	@DisplayName("Character 엔티티가 정상적으로 생성된다.")
	@Test
	void characterEntityCreationTest() {
		// given
		Group group = Group.builder()
			.writer(1L)
			.name("Test Group")
			.password("testPassword")
			.createdAt(LocalDateTime.now())
			.build();

		// when
		Character character = Character.builder()
			.group(group)
			.name("러키 모또")
			.rarity("1")
			.imageUrl("https://moddo-s3.s3.amazonaws.com/character/lucky-1.png")
			.imageBigUrl("https://moddo-s3.s3.amazonaws.com/character/lucky-1-big.png")
			.build();

		// then
		assertThat(character).isNotNull();
		assertThat(character.getGroup()).isEqualTo(group);
		assertThat(character.getName()).isEqualTo("러키 모또");
		assertThat(character.getRarity()).isEqualTo("1");
		assertThat(character.getImageUrl()).isEqualTo("https://moddo-s3.s3.amazonaws.com/character/lucky-1.png");
		assertThat(character.getImageBigUrl()).isEqualTo("https://moddo-s3.s3.amazonaws.com/character/lucky-1-big.png");
	}
}
