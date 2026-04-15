package com.dnd.moddo.auth.presentation;

import java.net.URI;
import java.util.Set;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthRedirectResolver {

	private static final String DEFAULT_LOCAL_REDIRECT_URL = "http://localhost:3000";
	private static final String DEFAULT_PROD_REDIRECT_URL = "https://www.moddo.kr";

	private static final Set<String> ALLOWED_REDIRECT_ORIGINS = Set.of(
		"http://localhost:3000",
		"https://moddo-frontend.pages.dev",
		"https://www.moddo.kr",
		"https://moddo.kr"
	);

	public String resolve(String state, HttpServletRequest request) {
		try {
			URI uri = URI.create(state);
			String origin = extractOrigin(uri);

			if (origin != null && ALLOWED_REDIRECT_ORIGINS.contains(origin)) {
				return uri.toString();
			}
		} catch (IllegalArgumentException ignored) {
			// Fallback to the default URL when state is not a valid redirect URL.
		}

		return isLocalRequest(request) ? DEFAULT_LOCAL_REDIRECT_URL : DEFAULT_PROD_REDIRECT_URL;
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
