package com.dnd.moddo.global.statistics.service.implementation;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.repository.ExpenseRepository;
import com.dnd.moddo.domain.settlement.repository.SettlementRepository;
import com.dnd.moddo.global.statistics.entity.DailyStats;
import com.dnd.moddo.global.statistics.repository.DailyStatsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyStatsCreator {
	private final SettlementRepository settlementRepository;
	private final ExpenseRepository expenseRepository;
	private final DailyStatsRepository dailyStatsRepository;

	public long getCreatedCount(LocalDateTime start, LocalDateTime end) {
		return settlementRepository.countCreatedSettlement(start, end);
	}

	public long getCompletedCount(LocalDateTime start, LocalDateTime end) {
		return settlementRepository.countCompletedSettlement(start, end);
	}

	public long getOverDueCount(LocalDateTime limit) {
		return settlementRepository.countOverdue(limit);
	}

	public Object[] getExpensesStats(LocalDateTime start, LocalDateTime end) {
		return expenseRepository.amountStats(start, end);
	}

	public DailyStats saveDailyStats(DailyStats dailyStats) {
		return dailyStatsRepository.save(dailyStats);
	}
}
