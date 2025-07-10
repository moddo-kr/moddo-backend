package com.dnd.moddo.domain.user.entity;

import java.time.LocalDateTime;

import com.dnd.moddo.domain.user.entity.type.Authority;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String email;

	private String profile;

	private Boolean isMember;

	private Long kakaoId;

	private LocalDateTime createdAt;

	private LocalDateTime expiredAt;

	@Enumerated(EnumType.STRING)
	private Authority authority;

	@Builder
	public User(String name, String email, String profile, Boolean isMember, Authority authority, Long kakaoId,
		LocalDateTime createdAt, LocalDateTime expiredAt) {
		this.name = name;
		this.email = email;
		this.profile = profile;
		this.isMember = isMember;
		this.kakaoId = kakaoId;
		this.createdAt = createdAt;
		this.expiredAt = expiredAt;
		this.authority = authority;
	}
}
