package com.dnd.moddo.domain.group.service.implementation;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.dnd.moddo.character.entity.Character;
import com.dnd.moddo.character.repository.CharacterRepository;
import com.dnd.moddo.domain.group.dto.request.GroupRequest;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import com.dnd.moddo.domain.image.dto.CharacterResponse;
import com.dnd.moddo.domain.image.service.implementation.ImageReader;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupCreator {

	private final GroupRepository groupRepository;
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final ImageReader imageReader;
	private final CharacterRepository characterRepository;

	public Group createGroup(GroupRequest request, Long userId) {
		User user = userRepository.getById(userId);
		String encryptedPassword = passwordEncoder.encode(request.password());

		Group group = Group.builder()
			.writer(user.getId())
			.name(request.name())
			.password(encryptedPassword)
			.createdAt(LocalDateTime.now())
			.build();

		group = groupRepository.save(group);

		CharacterResponse characterResponse = imageReader.getRandomCharacter();

		Character character = Character.builder()
			.group(group)
			.name(characterResponse.name())
			.rarity(characterResponse.rarity())
			.imageUrl(characterResponse.imageUrl())
			.imageBigUrl(characterResponse.imageBigUrl())
			.build();

		characterRepository.save(character);

		return group;
	}
}
