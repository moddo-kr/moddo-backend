package com.dnd.moddo.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao")
public record KakaoProperties(
	String redirectUri,
	String clientId,
	String tokenRequestUri,
	String profileRequestUri
) {

}
