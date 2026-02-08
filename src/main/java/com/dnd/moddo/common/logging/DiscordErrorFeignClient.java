package com.dnd.moddo.common.logging;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "error", url = "${discord.webhook-url}")
public interface DiscordErrorFeignClient {
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	void sendMessage(@RequestBody DiscordMessage discordMessage);
}
