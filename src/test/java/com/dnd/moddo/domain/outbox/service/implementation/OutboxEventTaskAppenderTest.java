package com.dnd.moddo.domain.outbox.service.implementation;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.outbox.application.impl.OutboxEventTaskAppender;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.task.EventTask;
import com.dnd.moddo.outbox.domain.event.type.AggregateType;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;
import com.dnd.moddo.outbox.domain.task.type.EventTaskType;
import com.dnd.moddo.outbox.infrastructure.EventTaskRepository;

@ExtendWith(MockitoExtension.class)
class OutboxEventTaskAppenderTest {

	@Mock
	private EventTaskRepository eventTaskRepository;

	@Mock
	private MemberReader memberReader;

	@InjectMocks
	private OutboxEventTaskAppender outboxEventTaskAppender;

	@Test
	@DisplayName("정산 완료 이벤트면 연결된 멤버마다 REWARD_GRANT와 NOTIFICATION_SEND 태스크를 생성한다.")
	void appendSettlementCompletedTasks() {
		OutboxEvent outboxEvent = OutboxEvent.pending(OutboxEventType.SETTLEMENT_COMPLETED, AggregateType.SETTLEMENT, 1L);
		Member firstMember = mock(Member.class);
		Member secondMember = mock(Member.class);

		when(firstMember.getUserId()).thenReturn(10L);
		when(secondMember.getUserId()).thenReturn(20L);
		when(memberReader.findAssignedMembersBySettlementId(1L)).thenReturn(List.of(firstMember, secondMember));

		outboxEventTaskAppender.appendTasks(outboxEvent);

		ArgumentCaptor<EventTask> captor = ArgumentCaptor.forClass(EventTask.class);
		verify(eventTaskRepository, times(4)).saveAndFlush(captor.capture());

		List<EventTask> savedTasks = captor.getAllValues();
		verifySavedTask(savedTasks.get(0), outboxEvent, EventTaskType.REWARD_GRANT, 10L);
		verifySavedTask(savedTasks.get(1), outboxEvent, EventTaskType.NOTIFICATION_SEND, 10L);
		verifySavedTask(savedTasks.get(2), outboxEvent, EventTaskType.REWARD_GRANT, 20L);
		verifySavedTask(savedTasks.get(3), outboxEvent, EventTaskType.NOTIFICATION_SEND, 20L);
	}

	private void verifySavedTask(EventTask eventTask, OutboxEvent outboxEvent, EventTaskType taskType, Long targetUserId) {
		org.assertj.core.api.Assertions.assertThat(eventTask.getOutboxEvent()).isEqualTo(outboxEvent);
		org.assertj.core.api.Assertions.assertThat(eventTask.getTaskType()).isEqualTo(taskType);
		org.assertj.core.api.Assertions.assertThat(eventTask.getTargetUserId()).isEqualTo(targetUserId);
	}
}
