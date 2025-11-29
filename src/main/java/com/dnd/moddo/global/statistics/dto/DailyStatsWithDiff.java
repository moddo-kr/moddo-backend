package com.dnd.moddo.global.statistics.dto;

public record DailyStatsWithDiff(
	int createdDiff,
	int completedDiff,
	int overdueDiff,
	long totalDiff,
	double avgDiff,
	long maxDiff,
	long minDiff
) {
}
