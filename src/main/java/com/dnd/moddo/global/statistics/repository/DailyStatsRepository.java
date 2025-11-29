package com.dnd.moddo.global.statistics.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.moddo.global.statistics.entity.DailyStats;

public interface DailyStatsRepository extends JpaRepository<DailyStats, Long> {
	Optional<DailyStats> getByDate(LocalDate date);
}
