package com.dnd.moddo.global.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.dnd.moddo.domain.auth.exception.TokenNotFoundException;
import com.dnd.moddo.domain.auth.exception.UserPermissionException;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.settlement.service.QuerySettlementService;
import com.dnd.moddo.domain.settlement.service.implementation.SettlementReader;
import com.dnd.moddo.global.common.annotation.VerifyManagerPermission;
import com.dnd.moddo.global.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Aspect
@Component
public class GroupPermissionAspect {
	private final JwtService jwtService;
	private final QuerySettlementService querySettlementService;
	private final HttpServletRequest request;
	private final SettlementReader settlementReader;

	@Before("@annotation(verifyManagerPermission)")
	public void checkPermission(JoinPoint joinPoint, VerifyManagerPermission verifyManagerPermission) {
		//헤더에서 user token 추출
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			throw new TokenNotFoundException("access token");
		}

		Long userId = jwtService.getUserId(request);

		//parameter에서 group token 추출
		String code = request.getParameter("groupToken");

		if (code == null) {
			throw new TokenNotFoundException("group token");
		}

		Long groupId = querySettlementService.findIdByCode(code);

		// 사용자 검증
		if (!isAuthorized(userId, groupId)) {
			throw new UserPermissionException();
		}
	}

	private boolean isAuthorized(Long userId, Long groupId) {
		Settlement settlement = settlementReader.read(groupId);
		return settlement.isWriter(userId);
	}
}
