package com.dnd.moddo.common.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dnd.moddo.auth.infrastructure.security.LoginUserArgumentResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	private final LoginUserArgumentResolver loginUserArgumentResolver;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("https://www.moddo.kr", "http://localhost:3000", "http://localhost:4173")
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
			.allowedHeaders("*")
			.exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
			.allowCredentials(true);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(loginUserArgumentResolver);
	}
}