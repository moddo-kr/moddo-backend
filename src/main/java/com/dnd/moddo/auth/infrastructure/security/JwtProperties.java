package com.dnd.moddo.auth.infrastructure.security;

import javax.crypto.SecretKey;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
	private final String header;
	private final String prefix;
	private final SecretKey secretKey;
	private final Long accessExpiration;
	private final Long refreshExpiration;

	public JwtProperties(String header, String prefix, String secretKey, Long accessExpiration,
		Long refreshExpiration) {
		this.header = header;
		this.prefix = prefix;
		this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
		this.accessExpiration = accessExpiration;
		this.refreshExpiration = refreshExpiration;
	}
}

