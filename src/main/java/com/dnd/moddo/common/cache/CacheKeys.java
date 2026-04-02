package com.dnd.moddo.common.cache;

import com.dnd.moddo.event.domain.member.type.MemberSortType;

public final class CacheKeys {
	private static final String SETTLEMENT_CODE_PREFIX = "settlements::";
	private static final String SETTLEMENT_LIST_PREFIX = "settlement-list::";
	private static final String SETTLEMENT_HEADER_PREFIX = "settlement-header::";
	private static final String MEMBERS_PREFIX = "members::";
	private static final String COLLECTIONS_PREFIX = "collections::";

	private CacheKeys() {
	}

	public static String settlementCode(String code) {
		return SETTLEMENT_CODE_PREFIX + code;
	}

	public static String settlementList(Long userId) {
		return SETTLEMENT_LIST_PREFIX + userId;
	}

	public static String settlementHeader(Long settlementId) {
		return SETTLEMENT_HEADER_PREFIX + settlementId;
	}

	public static String members(Long settlementId, MemberSortType sortType) {
		return MEMBERS_PREFIX + settlementId + "::" + sortType.name();
	}

	public static String membersPrefix(Long settlementId) {
		return MEMBERS_PREFIX + settlementId + "::";
	}

	public static String collections(Long userId) {
		return COLLECTIONS_PREFIX + userId;
	}
}
