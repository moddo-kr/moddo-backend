package com.dnd.moddo.event.infrastructure;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.dnd.moddo.event.domain.member.QMember;
import com.dnd.moddo.event.domain.settlement.QSettlement;
import com.dnd.moddo.event.domain.settlement.type.SettlementStatus;
import com.dnd.moddo.event.presentation.response.SettlementListResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SettlementQueryRepositoryImpl
	implements SettlementQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<SettlementListResponse> findByUserAndStatus(
		Long userId,
		SettlementStatus status
	) {
		QSettlement settlement = QSettlement.settlement;
		QMember member = QMember.member;

		// 내가 속한 정산 조건
		BooleanExpression userCondition =
			member.user.id.eq(userId);

		// 상태 조건
		BooleanExpression statusCondition = null;

		if (status == SettlementStatus.IN_PROGRESS) {
			statusCondition = settlement.completedAt.isNull();
		} else if (status == SettlementStatus.COMPLETED) {
			statusCondition = settlement.completedAt.isNotNull();
		}

		BooleanExpression finalCondition =
			statusCondition != null
				? userCondition.and(statusCondition)
				: userCondition;
		
		NumberExpression<Long> completedCount =
			Expressions.numberTemplate(
				Long.class,
				"sum(case when {0} = true then 1 else 0 end)",
				member.isPaid
			);

		return queryFactory
			.select(Projections.constructor(
				SettlementListResponse.class,
				settlement.id,
				settlement.code,
				settlement.name,
				member.id.count(),
				completedCount.coalesce(0L)
			))
			.from(member)
			.join(member.settlement, settlement)
			.where(finalCondition)
			.groupBy(
				settlement.id,
				settlement.code,
				settlement.name
			)
			.orderBy(settlement.createdAt.desc())
			.fetch();
	}

}
