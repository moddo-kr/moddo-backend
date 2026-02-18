package com.dnd.moddo.domain.image.entity;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.image.domain.Image;

class ImageTest {

	private Image image;

	@BeforeEach
	void setUp() {
		// given
		image = new Image("uniqueKey", "/images/path/to/image.jpg");
	}

	@Test
	void createImage() {
		// when
		String uniqueKey = image.getUniqueKey();
		String path = image.getPath();

		// then
		assertThat(uniqueKey).isEqualTo("uniqueKey");
		assertThat(path).isEqualTo("/images/path/to/image.jpg");
	}
}
