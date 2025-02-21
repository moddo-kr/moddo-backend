package com.dnd.moddo.domain.group.service.implementation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

	public Group createGroup(GroupRequest request, Long userId) {
		User user = userRepository.getById(userId);
		String encryptedPassword = passwordEncoder.encode(request.password());

		CharacterResponse character = imageReader.getRandomCharacter(null);
		List<String> characterUrls = List.of(character.imageUrl(), character.imageBigUrl());

		Group group = Group.builder()
			.writer(user.getId())
			.name(request.name())
			.password(encryptedPassword)
			.createdAt(LocalDateTime.now())
			.characterUrls(characterUrls)
			.build();

		return groupRepository.save(group);
	}
}
