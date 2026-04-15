package com.dnd.moddo.common.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "frontend")
public record FrontendProperties(
	List<String> corsAllowedOrigins,
	List<String> redirectAllowedOrigins,
	String defaultLocalRedirectUrl,
	String defaultProdRedirectUrl
) {
}
