package com.dnd.moddo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.global.statistics.scheduler.DailyStatsScheduler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/test")
public class ErrorTestController {
	private final DailyStatsScheduler dailyStatsScheduler;

	@GetMapping("/force-error")
	public String forceError() {
		throw new RuntimeException("🔥 강제 에러 발생 테스트");
	}

	@GetMapping("/force-stats")
	public void forceStats() {
		dailyStatsScheduler.sendDailyStats();
	}
}
