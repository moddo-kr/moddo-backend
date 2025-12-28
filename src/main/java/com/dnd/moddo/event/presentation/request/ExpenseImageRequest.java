package com.dnd.moddo.event.presentation.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record ExpenseImageRequest(
	@NotEmpty
	List<String> images
) {
}
