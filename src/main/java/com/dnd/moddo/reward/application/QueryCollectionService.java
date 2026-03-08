package com.dnd.moddo.reward.application;

import org.springframework.stereotype.Service;

import com.dnd.moddo.reward.application.impl.CollectionReader;
import com.dnd.moddo.reward.presentation.response.CollectionListResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryCollectionService {
	private final CollectionReader collectionReader;

	public CollectionListResponse findCollectionListByUserId(Long userId) {
		return collectionReader.findCollectionListByUserId(userId);
	}
}
