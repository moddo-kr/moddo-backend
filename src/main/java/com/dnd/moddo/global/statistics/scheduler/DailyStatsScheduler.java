package com.dnd.moddo.global.statistics.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dnd.moddo.global.logging.DailyStatsMessageBuilder;
import com.dnd.moddo.global.logging.DiscordMessage;
import com.dnd.moddo.global.logging.DiscordNotifier;
import com.dnd.moddo.global.statistics.dto.DailyStatsResult;
import com.dnd.moddo.global.statistics.dto.DailyStatsWithDiff;
import com.dnd.moddo.global.statistics.service.DailyStatsService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DailyStatsScheduler {

	private final DailyStatsService dailyStatsService;
	private final DailyStatsMessageBuilder messageBuilder;
	private final DiscordNotifier statsSender;

	// 매일 오전 9시
	@Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
	public void sendDailyStats() {

		DailyStatsResult today = dailyStatsService.getYesterdayStats();
		DailyStatsWithDiff dailyStatsWithDiff = dailyStatsService.getTodayStatsWithDiff(today);
		DiscordMessage message = messageBuilder.build(today, dailyStatsWithDiff);

		statsSender.sendStats(message);
	}
}
