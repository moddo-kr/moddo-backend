package com.dnd.moddo.domain.expense.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ExpenseImageRequest(
        @NotEmpty
        List<String> images
) {
}
