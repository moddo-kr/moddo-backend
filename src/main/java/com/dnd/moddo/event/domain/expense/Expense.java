package com.dnd.moddo.event.domain.expense;

import java.time.LocalDate;
import java.util.List;

import com.dnd.moddo.common.converter.StringListConverter;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Convert;
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
	@JoinColumn(name = "settlement_id")
	private Settlement settlement;

	private Long amount;

	private String content;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate date;

	@Convert(converter = StringListConverter.class)
	private List<String> images;

	public Expense(Settlement settlement, Long amount, String content, LocalDate date) {
		this(settlement, amount, content, date, null);
	}

	@Builder
	public Expense(Settlement settlement, Long amount, String content, LocalDate date, List<String> images) {
		this.settlement = settlement;
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
