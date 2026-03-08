package com.dnd.moddo.reward.presentation.response;

import java.time.LocalDateTime;

public record CollectionResponse(
	Long id,
	String name,
	int rarity,
	LocalDateTime acquiredAt,
	String imageUrl,
	String imageBigUrl
) {
}
