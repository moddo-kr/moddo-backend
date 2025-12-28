package com.dnd.moddo.common.support.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.dnd.moddo.common.support.VerifyManagerPermission;
import com.dnd.moddo.domain.auth.exception.TokenNotFoundException;
import com.dnd.moddo.domain.auth.exception.UserPermissionException;
import com.dnd.moddo.event.application.impl.SettlementReader;
import com.dnd.moddo.event.application.query.QuerySettlementService;
import com.dnd.moddo.event.domain.settlement.Settlement;
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

		Long settlementId = querySettlementService.findIdByCode(code);

		// 사용자 검증
		if (!isAuthorized(userId, settlementId)) {
			throw new UserPermissionException();
		}
	}

	private boolean isAuthorized(Long userId, Long settlementId) {
		Settlement settlement = settlementReader.read(settlementId);
		return settlement.isWriter(userId);
	}
}
