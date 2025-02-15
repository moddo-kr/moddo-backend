package com.dnd.moddo.domain.image.dto;

public record TempImageResponse(
        String uniqueKey
) {
    public static TempImageResponse from(String uniqueKey) {
        return new TempImageResponse(uniqueKey);
    }
}
