package com.dnd.moddo.reward.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.auth.infrastructure.security.LoginUser;
import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.reward.application.QueryCollectionService;
import com.dnd.moddo.reward.presentation.response.CollectionListResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/collections")
public class CollectionController {

	private final QueryCollectionService queryCollectionService;

	@GetMapping
	public CollectionListResponse getMyCollections(
		@LoginUser LoginUserInfo loginUser
	) {
		return queryCollectionService.findCollectionListByUserId(loginUser.userId());
	}
}