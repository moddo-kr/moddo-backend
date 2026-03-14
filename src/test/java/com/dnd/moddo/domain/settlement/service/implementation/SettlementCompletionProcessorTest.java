package com.dnd.moddo.domain.settlement.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.application.impl.SettlementCompletionProcessor;
import com.dnd.moddo.event.application.impl.SettlementUpdater;
import com.dnd.moddo.outbox.application.command.CommandOutboxEventService;
import com.dnd.moddo.outbox.domain.event.type.AggregateType;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;

@ExtendWith(MockitoExtension.class)
class SettlementCompletionProcessorTest {

	@Mock
	private MemberReader memberReader;

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
		verify(settlementUpdater, never()).complete(anyLong());
		verify(commandOutboxEventService, never()).create(any(), any(), anyLong());
	}

	@Test
	@DisplayName("정산이 방금 완료되면 아웃박스 이벤트를 생성한다.")
	void createsOutboxEventWhenSettlementCompleted() {
		when(memberReader.existsUnpaidMember(1L)).thenReturn(false);
		when(settlementUpdater.complete(1L)).thenReturn(true);

		boolean result = settlementCompletionProcessor.completeIfAllPaid(1L);

		assertThat(result).isTrue();
		verify(settlementUpdater).complete(1L);
		verify(commandOutboxEventService).create(OutboxEventType.SETTLEMENT_COMPLETED, AggregateType.SETTLEMENT, 1L);
	}

	@Test
	@DisplayName("이미 완료된 정산이면 아웃박스 이벤트를 생성하지 않는다.")
	void doesNotCreateOutboxEventWhenSettlementAlreadyCompleted() {
		when(memberReader.existsUnpaidMember(1L)).thenReturn(false);
		when(settlementUpdater.complete(1L)).thenReturn(false);

		boolean result = settlementCompletionProcessor.completeIfAllPaid(1L);

		assertThat(result).isFalse();
		verify(settlementUpdater).complete(1L);
		verify(commandOutboxEventService, never()).create(any(), any(), anyLong());
	}
}
