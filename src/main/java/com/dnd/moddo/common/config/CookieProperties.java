package com.dnd.moddo.common.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cookie")
public record CookieProperties(
	boolean httpOnly,
	boolean secure,
	String path,
	String sameSite,
	Duration maxAge
) {
}
