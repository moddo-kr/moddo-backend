package com.dnd.moddo.common.logging;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Profile("prod")
public class DiscordNotifier implements ErrorNotifier {
	private final DiscordErrorFeignClient errorClient;

	@Override
	public void notifyError(DiscordMessage msg) {
		errorClient.sendMessage(msg);
	}

}
