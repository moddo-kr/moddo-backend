package com.dnd.moddo.event.infrastructure;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.dnd.moddo.event.domain.member.QMember;
import com.dnd.moddo.event.domain.settlement.QSettlement;
import com.dnd.moddo.event.domain.settlement.type.SettlementSortType;
import com.dnd.moddo.event.domain.settlement.type.SettlementStatus;
import com.dnd.moddo.event.presentation.response.SettlementListResponse;
import com.querydsl.core.types.OrderSpecifier;
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
		SettlementStatus status,
		SettlementSortType sortType,
		int limit
	) {
		QSettlement settlement = QSettlement.settlement;
		QMember member = QMember.member;

		BooleanExpression userCondition =
			member.user.id.eq(userId);

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

		NumberExpression<Long> memberCount = member.id.count();

		NumberExpression<Long> completedCount =
			Expressions.numberTemplate(
				Long.class,
				"sum(case when {0} = true then 1 else 0 end)",
				member.isPaid
			);

		OrderSpecifier<?> orderSpecifier =
			getOrderSpecifier(sortType, settlement, memberCount, completedCount);

		return queryFactory
			.select(Projections.constructor(
				SettlementListResponse.class,
				settlement.id,
				settlement.code,
				settlement.name,
				memberCount,
				completedCount.coalesce(0L),
				settlement.createdAt,
				settlement.completedAt
			))
			.from(member)
			.join(member.settlement, settlement)
			.where(finalCondition)
			.groupBy(
				settlement.id,
				settlement.code,
				settlement.name
			)
			.orderBy(orderSpecifier)
			.limit(limit)
			.fetch();
	}

	private OrderSpecifier<?> getOrderSpecifier(
		SettlementSortType sortType,
		QSettlement settlement,
		NumberExpression<Long> memberCount,
		NumberExpression<Long> completedCount
	) {
		return switch (sortType) {
			case OLDEST -> settlement.createdAt.asc();
			case LATEST -> settlement.createdAt.desc();
			default -> settlement.createdAt.desc();
		};
	}
}
