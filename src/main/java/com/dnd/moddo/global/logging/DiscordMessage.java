package com.dnd.moddo.global.logging;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
public record DiscordMessage(String content, List<Embed> embeds) {
	public static DiscordMessage createDiscordMessage(String content, List<Embed> embeds) {
		return new DiscordMessage(content, embeds);
	}

	@Builder
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@Getter
	public static class Embed {

		private String title;
		private String description;
	}
}
