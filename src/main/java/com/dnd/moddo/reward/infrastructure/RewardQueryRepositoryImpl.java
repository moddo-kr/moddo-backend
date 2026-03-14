package com.dnd.moddo.reward.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.dnd.moddo.event.domain.settlement.QSettlement;
import com.dnd.moddo.reward.domain.character.Character;
import com.dnd.moddo.reward.domain.character.QCharacter;
import com.dnd.moddo.reward.domain.character.QCollection;
import com.dnd.moddo.reward.presentation.response.CollectionListResponse;
import com.dnd.moddo.reward.presentation.response.CollectionResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class RewardQueryRepositoryImpl implements RewardQueryRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public CollectionListResponse getCollectionListByUserId(Long userId) {

		QCollection collection = QCollection.collection;
		QCharacter character = QCharacter.character;

		List<CollectionResponse> response =
			queryFactory
				.select(
					Projections.constructor(
						CollectionResponse.class,
						character.id,
						character.name,
						character.rarity,
						collection.acquiredAt,
						new CaseBuilder()
							.when(collection.acquiredAt.isNull())
							.then(Expressions.nullExpression(String.class))
							.otherwise(character.imageUrl),
						new CaseBuilder()
							.when(collection.acquiredAt.isNull())
							.then(Expressions.nullExpression(String.class))
							.otherwise(character.imageBigUrl)
					)
				)
				.from(character)
				.leftJoin(collection)
				.on(
					collection.characterId.eq(character.id)
						.and(collection.userId.eq(userId))
				)
				.orderBy(character.rarity.asc())
				.fetch();

		return new CollectionListResponse(response);
	}

	@Override
	public Optional<Character> findBySettlementId(Long settlementId) {
		QCharacter character = QCharacter.character;
		QSettlement settlement = QSettlement.settlement;

		Character response = queryFactory
			.select(character)
			.from(settlement)
			.join(character).on(settlement.characterId.eq(character.id))
			.where(settlement.id.eq(settlementId))
			.fetchOne();

		return Optional.ofNullable(response);
	}

}
