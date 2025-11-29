package com.dnd.moddo.global.statistics.dto;

import java.time.LocalDate;

import com.dnd.moddo.global.statistics.entity.DailyStats;

import lombok.Builder;

@Builder
public record DailyStatsResult(
	LocalDate date,
	long createdCount,
	long completedCount,
	long overdueCount,

	Long totalAmount,
	Double avgAmount,
	Long maxAmount,
	Long minAmount,

	long totalGroupMember,
	double avgGroupMember,
	long maxGroupMember,
	long minGroupMember

) {

	public static DailyStatsResult of(DailyStats dailyStats) {
		return DailyStatsResult.builder()
			.date(dailyStats.getDate())
			.createdCount(dailyStats.getCreatedCount())
			.completedCount(dailyStats.getCompletedCount())
			.overdueCount(dailyStats.getOverdueCount())
			.totalAmount(dailyStats.getTotalAmount())
			.avgAmount(dailyStats.getAvgAmount())
			.maxAmount(dailyStats.getMaxAmount())
			.minAmount(dailyStats.getMinAmount())
			.build();
	}
}
