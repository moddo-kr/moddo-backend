package com.dnd.moddo.global.statistics.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.dnd.moddo.global.statistics.dto.DailyStatsResult;
import com.dnd.moddo.global.statistics.dto.DailyStatsWithDiff;
import com.dnd.moddo.global.statistics.entity.DailyStats;
import com.dnd.moddo.global.statistics.service.implementation.DailyStatsCreator;
import com.dnd.moddo.global.statistics.service.implementation.DailyStatsReader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyStatsService {
	//humm... command query...
	private final DailyStatsCreator dailyStatsCreator;
	private final DailyStatsReader dailyStatsReader;

	public DailyStatsResult getYesterdayStats() {

		LocalDate date = LocalDate.now().minusDays(1);
		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = date.plusDays(1).atStartOfDay();

		long created = dailyStatsCreator.getCreatedCount(start, end);
		long completed = dailyStatsCreator.getCompletedCount(start, end);
		long overdue = dailyStatsCreator.getOverDueCount(LocalDateTime.now().minusHours(24));

		Object[] raw = dailyStatsCreator.getExpensesStats(start, end);

		long total = 0L;
		double avg = 0.0;
		long min = 0L;
		long max = 0L;

		if (raw != null && raw.length > 0) {
			Object[] row = (Object[])raw[0];

			total = row[0] instanceof Number ? ((Number)row[0]).longValue() : 0L;
			avg = row[1] instanceof Number ? ((Number)row[1]).doubleValue() : 0.0;
			max = row[2] instanceof Number ? ((Number)row[2]).longValue() : 0L;
			min = row[3] instanceof Number ? ((Number)row[3]).longValue() : 0L;
		}

		DailyStats dailyStats = new DailyStats(date, created, completed, overdue, total, avg, max, min);

		dailyStatsCreator.saveDailyStats(dailyStats);
		return DailyStatsResult.of(dailyStats);
	}

	public DailyStatsWithDiff getTodayStatsWithDiff(DailyStatsResult today) {

		DailyStatsResult yesterday = dailyStatsReader.getDailyStats(today.date().minusDays(1));

		int createdDiff = 0;
		int completedDiff = 0;
		int overdueDiff = 0;

		long totalDiff = 0;
		double avgDiff = 0;
		long maxDiff = 0;
		long minDiff = 0;

		if (yesterday != null) {
			createdDiff = (int)(today.createdCount() - yesterday.createdCount());
			completedDiff = (int)(today.completedCount() - yesterday.completedCount());
			overdueDiff = (int)(today.overdueCount() - yesterday.overdueCount());

			totalDiff = today.totalAmount() - yesterday.totalAmount();
			avgDiff = today.avgAmount() - yesterday.avgAmount();
			maxDiff = today.maxAmount() - yesterday.maxAmount();
			minDiff = today.minAmount() - yesterday.minAmount();
		}

		return new DailyStatsWithDiff(createdDiff, completedDiff, overdueDiff,
			totalDiff, avgDiff, maxDiff, minDiff);
	}

}
