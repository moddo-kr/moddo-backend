package com.dnd.moddo.global.statistics.service.implementation;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.dnd.moddo.global.statistics.dto.DailyStatsResult;
import com.dnd.moddo.global.statistics.entity.DailyStats;
import com.dnd.moddo.global.statistics.repository.DailyStatsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyStatsReader {
	private final DailyStatsRepository dailyStatsRepository;

	public DailyStatsResult getDailyStats(LocalDate date) {
		DailyStats dailyStats = dailyStatsRepository.getByDate(date)
			.orElseThrow(RuntimeException::new);
		return DailyStatsResult.of(dailyStats);
	}
}
