package com.dnd.moddo.domain.expense.entity;

import java.time.LocalDate;
import java.util.List;

import com.dnd.moddo.domain.group.entity.Group;
import com.fasterxml.jackson.annotation.JsonFormat;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "expenses")
@Entity
public class Expense {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private Group group;

	private Long amount;

	private String content;

	@Column(name = "`order`")
	private Integer order;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate date;

	//TODO List 직렬화 @Convert 추가하기
	private List<String> images;

	public Expense(Group group, Long amount, String content, int order, LocalDate date) {
		this(group, amount, content, order, date, null);
	}

	@Builder
	public Expense(Group group, Long amount, String content, int order, LocalDate date, List<String> images) {
		this.group = group;
		this.amount = amount;
		this.content = content;
		this.order = order;
		this.date = date;
		this.images = images;
	}

	public void updateOrder(int order) {
		this.order = order;
	}

	public void update(Long amount, String content, LocalDate date) {
		this.amount = amount;
		this.content = content;
		this.date = date;
	}
}
