package com.dnd.moddo.domain.groupMember.entity;

import com.dnd.moddo.domain.group.entity.Group;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private Group group;

	@Column(name = "is_paid", nullable = false)
	private boolean isPaid;

	public GroupMember(String name, Group group) {
		this(null, name, null, group, false);
	}

	public GroupMember(String name, Integer profileId, Group group) {
		this(null, name, profileId, group, false);
	}

	public GroupMember(Long id, String name, Integer profileId, Group group, boolean isPaid) {
		this.id = id;
		this.name = name;
		this.profileId = profileId;
		this.group = group;
		this.isPaid = isPaid;
	}

}

