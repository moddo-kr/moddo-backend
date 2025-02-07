package com.dnd.moddo.global.jwt.properties;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private final String header;
    private final String prefix;
    private final SecretKey secretKey;
    private final Long accessExpiration;
    private final Long refreshExpiration;

    public JwtProperties(String header, String prefix, String secretKey, Long accessExpiration, Long refreshExpiration) {
        this.header = header;
        this.prefix = prefix;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }
}

