package com.dnd.moddo.image.presentation.response;

import java.util.List;

public record TempImageResponse(
	List<String> uniqueKeys
) {
	public static TempImageResponse from(List<String> uniqueKeys) {
		return new TempImageResponse(uniqueKeys);
	}
}