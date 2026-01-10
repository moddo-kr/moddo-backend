package com.dnd.moddo.domain.character.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.domain.character.service.QueryCharacterService;
import com.dnd.moddo.domain.image.dto.CharacterResponse;
import com.dnd.moddo.event.application.query.QuerySettlementService;
import com.dnd.moddo.global.jwt.service.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/character")
public class CharacterController {

	private final JwtService jwtService;
	private final QueryCharacterService queryCharacterService;
	private final QuerySettlementService querySettlementService;

	@GetMapping()
	public ResponseEntity<CharacterResponse> getCharacter(
		@RequestParam("groupToken") String code
	) {
		Long groupId = querySettlementService.findIdByCode(code);

		CharacterResponse response = queryCharacterService.findCharacterByGroupId(groupId);
		return ResponseEntity.ok(response);
	}
}
