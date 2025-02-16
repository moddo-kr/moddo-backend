package com.dnd.moddo.domain.image.dto;

import java.util.List;

public record ImageResponse(
        List<String> paths
) {
    public static ImageResponse from(List<String> paths) {
        return new ImageResponse(paths);
    }
}