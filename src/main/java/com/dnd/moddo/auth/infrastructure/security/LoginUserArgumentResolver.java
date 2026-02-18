package com.dnd.moddo.auth.infrastructure.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.dnd.moddo.auth.infrastructure.security.exception.MissingTokenException;
import com.dnd.moddo.auth.model.AuthDetails;
import com.dnd.moddo.auth.presentation.response.LoginUserInfo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(LoginUser.class)
			&& parameter.getParameterType().equals(LoginUserInfo.class);
	}

	@Override
	public Object resolveArgument(
		MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) {

		Authentication authentication =
			SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null
			|| !authentication.isAuthenticated()
			|| !(authentication.getPrincipal() instanceof AuthDetails authDetails)) {
			throw new MissingTokenException();
		}

		return new LoginUserInfo(
			authDetails.getUserId(),
			authDetails.getRole()
		);
	}

}