package com.dnd.moddo.domain.settlement.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.application.impl.SettlementCompletionProcessor;
import com.dnd.moddo.event.application.impl.SettlementReader;
import com.dnd.moddo.event.application.impl.SettlementUpdater;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.outbox.application.command.CommandOutboxEventService;
import com.dnd.moddo.outbox.domain.event.type.AggregateType;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;

@ExtendWith(MockitoExtension.class)
class SettlementCompletionProcessorTest {

	@Mock
	private MemberReader memberReader;

	@Mock
	private SettlementReader settlementReader;

	@Mock
	private SettlementUpdater settlementUpdater;

	@Mock
	private CommandOutboxEventService commandOutboxEventService;

	@InjectMocks
	private SettlementCompletionProcessor settlementCompletionProcessor;

	@Test
	@DisplayName("미납 멤버가 있으면 정산을 완료하지 않는다.")
	void doesNotCompleteWhenUnpaidMemberExists() {
		when(memberReader.existsUnpaidMember(1L)).thenReturn(true);

		boolean result = settlementCompletionProcessor.completeIfAllPaid(1L);

		assertThat(result).isFalse();
		verify(settlementUpdater, never()).complete(anyLong(), any(LocalDateTime.class));
		verify(commandOutboxEventService, never()).create(any(), any(), anyLong());
	}

	@Test
	@DisplayName("정산이 방금 완료되면 아웃박스 이벤트를 생성한다.")
	void createsOutboxEventWhenSettlementCompleted() {
		Settlement settlement = createSettlement(LocalDateTime.now().plusDays(1));

		when(memberReader.existsUnpaidMember(1L)).thenReturn(false);
		when(settlementReader.read(1L)).thenReturn(settlement);
		when(settlementUpdater.complete(eq(1L), any(LocalDateTime.class))).thenReturn(true);

		boolean result = settlementCompletionProcessor.completeIfAllPaid(1L);

		assertThat(result).isTrue();
		verify(settlementUpdater).complete(eq(1L), any(LocalDateTime.class));
		verify(commandOutboxEventService).create(OutboxEventType.SETTLEMENT_COMPLETED, AggregateType.SETTLEMENT, 1L);
	}

	@Test
	@DisplayName("이미 완료된 정산이면 아웃박스 이벤트를 생성하지 않는다.")
	void doesNotCreateOutboxEventWhenSettlementAlreadyCompleted() {
		Settlement settlement = createSettlement(LocalDateTime.now().plusDays(1));

		when(memberReader.existsUnpaidMember(1L)).thenReturn(false);
		when(settlementReader.read(1L)).thenReturn(settlement);
		when(settlementUpdater.complete(eq(1L), any(LocalDateTime.class))).thenReturn(false);

		boolean result = settlementCompletionProcessor.completeIfAllPaid(1L);

		assertThat(result).isFalse();
		verify(settlementUpdater).complete(eq(1L), any(LocalDateTime.class));
		verify(commandOutboxEventService, never()).create(any(), any(), anyLong());
	}

	@Test
	@DisplayName("마감일 이후 완료된 정산이면 아웃박스 이벤트를 생성하지 않는다.")
	void givenCompletedAfterDeadline_thenDoesNotCreateOutboxEvent() {
		Settlement settlement = createSettlement(LocalDateTime.now().minusSeconds(1));

		when(memberReader.existsUnpaidMember(1L)).thenReturn(false);
		when(settlementReader.read(1L)).thenReturn(settlement);
		when(settlementUpdater.complete(eq(1L), any(LocalDateTime.class))).thenReturn(true);

		boolean result = settlementCompletionProcessor.completeIfAllPaid(1L);

		assertThat(result).isTrue();
		verify(settlementUpdater).complete(eq(1L), any(LocalDateTime.class));
		verify(commandOutboxEventService, never()).create(any(), any(), anyLong());
	}

	private Settlement createSettlement(LocalDateTime deadline) {
		return Settlement.builder()
			.name("group")
			.writer(1L)
			.createdAt(LocalDateTime.now().minusDays(1))
			.code("code")
			.deadline(deadline)
			.build();
	}
}
