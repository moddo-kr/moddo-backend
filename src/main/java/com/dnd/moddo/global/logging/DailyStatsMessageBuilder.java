package com.dnd.moddo.global.logging;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dnd.moddo.global.statistics.dto.DailyStatsResult;
import com.dnd.moddo.global.statistics.dto.DailyStatsWithDiff;

@Component
public class DailyStatsMessageBuilder {

	public DiscordMessage build(DailyStatsResult today, DailyStatsWithDiff yesterday) {

		return DiscordMessage.builder()
			.content("# 📊 **MODDO Daily Settlement Report (%s)**".formatted(today.date()))
			.embeds(List.of(
				DiscordMessage.Embed.builder()
					.title("")
					.description(
						"""
							## 정산 현황
							
							### 생성된 정산  
							→ **%d건** (전일 대비 %+d)
							
							### 완료된 정산  
							→ **%d건** (전일 대비 %+d)
							
							### 24시간 이상 미완료
							→ **%d건** (전일 대비 %+d)
							
							---
							
							## 지출 금액 통계
							
							### 총합
							→ **%,d원** (전일 대비 %+d)
							
							### 평균
							→ **%,.0f원** (전일 대비 %+.0f)
							
							### 최대
							→ **%,d원** (전일 대비 %+d)
							
							### 최소
							→ **%,d원** (전일 대비 %+d)
							""".formatted(

							today.createdCount(), yesterday.createdDiff(),
							today.completedCount(), yesterday.completedDiff(),
							today.overdueCount(), yesterday.overdueDiff(),
							today.totalAmount(), yesterday.totalDiff(),
							today.avgAmount(), yesterday.avgDiff(),
							today.maxAmount(), yesterday.maxDiff(),
							today.minAmount(), yesterday.minDiff()
						)
					)
					.build()
			))
			.build();

	}
}
