package com.dnd.moddo.global.statistics.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "daily_stats")
public class DailyStats {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate date;

	private long createdCount;
	private long completedCount;
	private long overdueCount;

	private Long totalAmount;
	private Double avgAmount;
	private Long maxAmount;
	private Long minAmount;

	public DailyStats(LocalDate date, long createdCount, long completedCount, long overdueCount, Long totalAmount,
		Double avgAmount, Long maxAmount, Long minAmount) {
		this.date = date;
		this.createdCount = createdCount;
		this.completedCount = completedCount;
		this.overdueCount = overdueCount;
		this.totalAmount = totalAmount;
		this.avgAmount = avgAmount;
		this.maxAmount = maxAmount;
		this.minAmount = minAmount;
	}
}
