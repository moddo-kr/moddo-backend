package com.dnd.moddo.domain.settlement.service.implementation;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.character.entity.Character;
import com.dnd.moddo.domain.character.repository.CharacterRepository;
import com.dnd.moddo.domain.image.dto.CharacterResponse;
import com.dnd.moddo.domain.image.service.implementation.ImageReader;
import com.dnd.moddo.domain.settlement.dto.request.SettlementRequest;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.settlement.repository.SettlementRepository;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;
import com.dnd.moddo.global.util.ShortUUIDGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettlementCreator {

	private final SettlementRepository settlementRepository;
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final ImageReader imageReader;
	private final CharacterRepository characterRepository;

	public Settlement createSettlement(SettlementRequest request, Long userId) {
		User user = userRepository.getById(userId);
		String encryptedPassword = passwordEncoder.encode(request.password());

		Settlement settlement = Settlement.builder()
			.writer(user.getId())
			.name(request.name())
			.password(encryptedPassword)
			.createdAt(LocalDateTime.now())
			.code(generateUniqueGroupCode())
			.build();

		settlement = settlementRepository.save(settlement);

		CharacterResponse characterResponse = imageReader.getRandomCharacter();

		Character character = Character.builder()
			.settlement(settlement)
			.name(characterResponse.name())
			.rarity(characterResponse.rarity())
			.imageUrl(characterResponse.imageUrl())
			.imageBigUrl(characterResponse.imageBigUrl())
			.build();

		characterRepository.save(character);

		return settlement;
	}

	private String generateUniqueGroupCode() {
		for (int i = 0; i < 5; i++) {
			String uuid = UUID.randomUUID().toString();
			String code = ShortUUIDGenerator.shortenUUID(uuid);
			if (!settlementRepository.existsByCode(code)) {
				return code;
			}
		}
		throw new RuntimeException("고유 코드 저장 실패");
	}

}
