package com.dnd.moddo.domain.groupMember.entity;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

	@Enumerated(EnumType.STRING)
	private ExpenseRole role;

	public GroupMember(String name, Group group, ExpenseRole role) {
		this(null, name, null, group, false, role);
	}

	public GroupMember(String name, Integer profileId, Group group, ExpenseRole role) {
		this(null, name, profileId, group, false, role);
	}

	public GroupMember(Long id, String name, Integer profileId, Group group, boolean isPaid, ExpenseRole role) {
		this.id = id;
		this.name = name;
		this.profileId = profileId;
		this.group = group;
		this.isPaid = isPaid;
		this.role = role;
	}

	public boolean isManager() {
		return ExpenseRole.MANAGER.equals(role);
	}
}

