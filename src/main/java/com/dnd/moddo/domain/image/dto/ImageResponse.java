package com.dnd.moddo.domain.image.dto;

public record ImageResponse(
        String path
) {
    public static ImageResponse from(String path) {
        return new ImageResponse(path);
    }
}
