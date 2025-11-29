package com.dnd.moddo.global.logging;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${discord.stats-name}", url = "${discord.stats-webhook-url}")
public interface DiscordStatsFeignClient {
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	void sendMessage(@RequestBody DiscordMessage discordMessage);
}
