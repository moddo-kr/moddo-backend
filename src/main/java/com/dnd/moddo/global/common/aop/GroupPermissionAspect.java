package com.dnd.moddo.global.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.dnd.moddo.domain.auth.exception.TokenNotFoundException;
import com.dnd.moddo.domain.auth.exception.UserPermissionException;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
import com.dnd.moddo.global.common.annotation.VerifyManagerPermission;
import com.dnd.moddo.global.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Aspect
@Component
public class GroupPermissionAspect {
	private final JwtService jwtService;
	private final HttpServletRequest request;
	private final GroupReader groupReader;

	@Before("@annotation(verifyManagerPermission)")
	public void checkPermission(JoinPoint joinPoint, VerifyManagerPermission verifyManagerPermission) {
		//헤더에서 user token 추출
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			throw new TokenNotFoundException("access token");
		}

		Long userId = jwtService.getUserId(request);

		//parameter에서 group token 추출
		String groupToken = request.getParameter("groupToken");

		if (groupToken == null) {
			throw new TokenNotFoundException("group token");
		}

		Long groupId = jwtService.getGroupId(groupToken);

		// 사용자 검증
		if (!isAuthorized(userId, groupId)) {
			throw new UserPermissionException();
		}
	}

	private boolean isAuthorized(Long userId, Long groupId) {
		Group group = groupReader.read(groupId);
		return group.isWriter(userId);
	}
}
