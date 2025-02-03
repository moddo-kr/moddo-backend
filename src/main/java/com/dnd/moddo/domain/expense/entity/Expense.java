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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "expense")
@Entity
public class Expense {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long meetId;

	private Double amount;

	private String content;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate date;

	//TODO List 직렬화 @Convert 추가하기
	private List<String> images;

	public Expense(Long meetId, Double amount, String content, LocalDate date) {
		this(null, meetId, amount, content, date, null);
	}

	public Expense(Long id, Long meetId, Double amount, String content, LocalDate date, List<String> images) {
		this.id = id;
		this.meetId = meetId;
		this.amount = amount;
		this.content = content;
		this.date = date;
		this.images = images;
	}

	public void update(Double amount, String content, LocalDate date) {
		this.amount = amount;
		this.content = content;
		this.date = date;
	}
}
