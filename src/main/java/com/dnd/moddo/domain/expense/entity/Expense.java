package com.dnd.moddo.domain.expense.entity;

import java.time.LocalDate;
import java.util.List;

import ch.qos.logback.core.testUtil.StringListAppender;
import com.dnd.moddo.domain.expense.dto.request.ExpenseImageRequest;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.global.converter.StringListConverter;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;
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

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate date;

	@Convert(converter = StringListConverter.class)
	private List<String> images;

	public Expense(Group group, Long amount, String content, LocalDate date) {
		this(group, amount, content, date, null);
	}

	@Builder
	public Expense(Group group, Long amount, String content, LocalDate date, List<String> images) {
		this.group = group;
		this.amount = amount;
		this.content = content;
		this.date = date;
		this.images = images;
	}

	public void update(Long amount, String content, LocalDate date) {
		this.amount = amount;
		this.content = content;
		this.date = date;
	}

	public void updateImgUrl(List<String> images) {
		this.images = images;
	}
}
