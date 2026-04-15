package com.dnd.moddo.auth.presentation;

import java.net.URI;
import org.springframework.stereotype.Component;

import com.dnd.moddo.common.config.FrontendProperties;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthRedirectResolver {

	private final FrontendProperties frontendProperties;

	public String resolve(String state, HttpServletRequest request) {
		try {
			URI uri = URI.create(state);
			String origin = extractOrigin(uri);

			if (origin != null && frontendProperties.redirectAllowedOrigins().contains(origin)) {
				return uri.toString();
			}
		} catch (IllegalArgumentException ignored) {
			// Fallback to the default URL when state is not a valid redirect URL.
		}

		return isLocalRequest(request)
			? frontendProperties.defaultLocalRedirectUrl()
			: frontendProperties.defaultProdRedirectUrl();
	}

	private String extractOrigin(URI uri) {
		if (!uri.isAbsolute() || uri.getScheme() == null || uri.getHost() == null) {
			return null;
		}

		StringBuilder origin = new StringBuilder()
			.append(uri.getScheme().toLowerCase())
			.append("://")
			.append(uri.getHost().toLowerCase());

		if (uri.getPort() != -1) {
			origin.append(":").append(uri.getPort());
		}

		return origin.toString();
	}

	private boolean isLocalRequest(HttpServletRequest request) {
		String serverName = request.getServerName();
		return "localhost".equalsIgnoreCase(serverName) || "127.0.0.1".equals(serverName);
	}
}
