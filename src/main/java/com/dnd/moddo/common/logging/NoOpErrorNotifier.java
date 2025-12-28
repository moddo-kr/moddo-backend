package com.dnd.moddo.common.logging;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"})
public class NoOpErrorNotifier implements ErrorNotifier {

	@Override
	public void notifyError(DiscordMessage message) {

	}
}