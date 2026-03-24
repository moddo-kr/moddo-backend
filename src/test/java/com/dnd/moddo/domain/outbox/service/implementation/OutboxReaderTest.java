package com.dnd.moddo.domain.outbox.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import com.dnd.moddo.outbox.application.impl.OutboxReader;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventStatus;
import com.dnd.moddo.outbox.infrastructure.OutboxEventRepository;

@ExtendWith(MockitoExtension.class)
class OutboxReaderTest {

	@Mock
	private OutboxEventRepository outboxEventRepository;

	@InjectMocks
	private OutboxReader outboxReader;

	@Test
	@DisplayName("상태로 아웃박스 이벤트 목록을 배치 조회한다.")
	void findByStatus() {
		OutboxEvent first = mock(OutboxEvent.class);
		OutboxEvent second = mock(OutboxEvent.class);
		PageRequest pageRequest = PageRequest.of(0, 100);
		Slice<OutboxEvent> events = new PageImpl<>(java.util.List.of(first, second), pageRequest, 2);
		when(outboxEventRepository.findByStatusOrderByIdAsc(OutboxEventStatus.PENDING, pageRequest)).thenReturn(events);

		Slice<OutboxEvent> result = outboxReader.findByStatus(OutboxEventStatus.PENDING, pageRequest);

		assertThat(result.getContent()).containsExactly(first, second);
	}

	@Test
	@DisplayName("아이디로 아웃박스 이벤트를 조회한다.")
	void findById() {
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		when(outboxEventRepository.getById(1L)).thenReturn(outboxEvent);

		OutboxEvent result = outboxReader.findById(1L);

		assertThat(result).isEqualTo(outboxEvent);
	}
}
