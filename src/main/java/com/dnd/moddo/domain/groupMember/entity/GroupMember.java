package com.dnd.moddo.domain.groupMember.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "group_members")
@Entity
public class GroupMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", updatable = false, nullable = false)
	private String name;

	@Column(name = "profile_id")
	private Integer profileId;

	@Column(name = "meet_id", updatable = false, nullable = false)
	private Long groupId;

	@Column(name = "is_paid", nullable = false)
	private boolean isPaid;

	public GroupMember(String name, Long groupId) {
		this(null, name, null, groupId, false);
	}

	public GroupMember(String name, Integer profileId, Long groupId) {
		this(null, name, profileId, groupId, false);
	}

	public GroupMember(Long id, String name, Integer profileId, Long groupId, boolean isPaid) {
		this.id = id;
		this.name = name;
		this.profileId = profileId;
		this.groupId = groupId;
		this.isPaid = isPaid;
	}

}

