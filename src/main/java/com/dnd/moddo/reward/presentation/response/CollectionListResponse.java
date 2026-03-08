package com.dnd.moddo.reward.presentation.response;

import java.util.List;

public record CollectionListResponse(
	List<CollectionResponse> collections
) {
}
