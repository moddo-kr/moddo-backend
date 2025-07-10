package com.dnd.moddo.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.fasterxml.jackson.annotation.JsonProperty;

@ConfigurationProperties(prefix = "kakao")
public record KakaoProperties(
	@JsonProperty("redirect_uri") String redirectUri,
	@JsonProperty("client_id") String client_id
) {

}
