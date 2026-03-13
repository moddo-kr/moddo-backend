package com.dnd.moddo.common.support.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.dnd.moddo.auth.infrastructure.security.exception.MissingTokenException;
import com.dnd.moddo.auth.model.AuthDetails;
import com.dnd.moddo.auth.model.exception.UserPermissionException;
import com.dnd.moddo.common.support.VerifyManagerPermission;
import com.dnd.moddo.event.application.impl.SettlementReader;
import com.dnd.moddo.event.application.query.QuerySettlementService;
import com.dnd.moddo.event.domain.settlement.Settlement;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class GroupPermissionAspect {

	private final QuerySettlementService querySettlementService;
	private final SettlementReader settlementReader;

	@Before("@annotation(verifyManagerPermission)")
	public void checkPermission(JoinPoint joinPoint, VerifyManagerPermission verifyManagerPermission) {

		Long userId = extractUserId();
		String code = extractCode(joinPoint.getArgs());

		Long settlementId = querySettlementService.findIdByCode(code);

		if (!isAuthorized(userId, settlementId)) {
			throw new UserPermissionException();
		}
	}

	private Long extractUserId() {
		Authentication authentication =
			SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null
			|| !(authentication.getPrincipal() instanceof AuthDetails authDetails)) {
			throw new MissingTokenException();
		}

		return authDetails.getUserId();
	}

	private String extractCode(Object[] args) {
		for (Object arg : args) {
			if (arg instanceof String token) {
				return token;
			}
		}
		throw new IllegalArgumentException("code parameter not found");
	}

	private boolean isAuthorized(Long userId, Long settlementId) {
		Settlement settlement = settlementReader.read(settlementId);
		return settlement.isWriter(userId);
	}
}
