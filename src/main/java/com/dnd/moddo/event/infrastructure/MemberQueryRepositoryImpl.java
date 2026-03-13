package com.dnd.moddo.event.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.member.QMember;
import com.dnd.moddo.event.domain.member.type.MemberSortType;
import com.dnd.moddo.event.presentation.response.MemberResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Member> findAllBySettlementId(Long settlementId, MemberSortType sortType) {
		QMember member = QMember.member;

		return queryFactory
			.selectFrom(member)
			.where(member.settlement.id.eq(settlementId))
			.orderBy(getOrderSpecifiers(member, sortType))
			.fetch();
	}

	@Override
	public Map<Long, List<MemberResponse>> findMembersByIds(List<Long> settlementIds) {

		if (settlementIds.isEmpty()) {
			return Map.of();
		}

		QMember member = QMember.member;

		List<MemberFlatProjection> results = queryFactory
			.select(
				Projections.constructor(
					MemberFlatProjection.class,
					member.settlement.id,
					member.id,
					member.role,
					member.name,
					member.user.id
				)
			)
			.from(member)
			.where(member.settlement.id.in(settlementIds))
			.fetch();

		return results.stream()
			.collect(Collectors.groupingBy(
				MemberFlatProjection::getSettlementId,
				Collectors.mapping(
					p -> new MemberResponse(
						p.getMemberId(),
						p.getRole(),
						p.getName(),
						p.getUserId()
					),
					Collectors.toList()
				)
			));
	}

	private OrderSpecifier<?>[] getOrderSpecifiers(QMember member, MemberSortType sortType) {
		OrderSpecifier<Integer> managerFirst = new CaseBuilder()
			.when(member.role.eq(ExpenseRole.MANAGER)).then(0)
			.otherwise(1)
			.asc();

		return switch (sortType) {
			case NAME -> new OrderSpecifier[] {
				managerFirst,
				member.name.asc(),
				member.id.asc()
			};
			case PAID_AT -> new OrderSpecifier[] {
				managerFirst,
				member.paidAt.asc().nullsLast(),
				member.id.asc()
			};
			case CREATED -> new OrderSpecifier[] {
				managerFirst,
				member.id.asc()
			};
		};
	}

	/**
	 * Repository 내부 전용 Projection
	 */
	@Getter
	public static class MemberFlatProjection {
		Long settlementId;
		Long memberId;
		ExpenseRole role;
		String name;
		Long userId;

		public MemberFlatProjection(Long settlementId, Long memberId, ExpenseRole role, String name, Long userId) {
			this.settlementId = settlementId;
			this.memberId = memberId;
			this.role = role;
			this.name = name;
			this.userId = userId;
		}
	}
}
