package com.dnd.moddo.domain.expense.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	private Long groupId;

	private Long amount;

	private String content;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate date;

	//TODO List 직렬화 @Convert 추가하기
	private List<String> images;

	public Expense(Long groupId, Long amount, String content, LocalDate date) {
		this(groupId, amount, content, date, null);
	}

	@Builder
	public Expense(Long groupId, Long amount, String content, LocalDate date, List<String> images) {
		this.groupId = groupId;
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
}
