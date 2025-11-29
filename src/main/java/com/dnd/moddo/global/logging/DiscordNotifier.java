package com.dnd.moddo.global.logging;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DiscordNotifier {
	private final DiscordErrorFeignClient errorClient;
	private final DiscordStatsFeignClient statsClient;

	public void sendError(DiscordMessage msg) {
		errorClient.sendMessage(msg);
	}

	public void sendStats(DiscordMessage msg) {
		statsClient.sendMessage(msg);
	}
}
