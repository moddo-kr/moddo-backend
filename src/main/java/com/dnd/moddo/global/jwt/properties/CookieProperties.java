package com.dnd.moddo.global.jwt.properties;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cookie")
public record CookieProperties(
	boolean httpOnly,
	boolean secure,
	String domain,
	String path,
	String sameSite,
	Duration maxAge
) {
}
