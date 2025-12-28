package com.dnd.moddo.domain.settlement.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.SettlementUpdater;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.domain.settlement.exception.GroupNotFoundException;
import com.dnd.moddo.event.infrastructure.SettlementRepository;
import com.dnd.moddo.event.presentation.request.request.SettlementAccountRequest;

@ExtendWith(MockitoExtension.class)
class SettlementUpdaterTest {
	@Mock
	private SettlementRepository settlementRepository;
	@InjectMocks
	private SettlementUpdater settlementUpdater;

	@DisplayName("그룹이 존재하면 계좌 정보를 수정할 수 있다.")
	@Test
	void updateAccountSuccess() {
		// given
		Long groupId = 1L;
		Settlement mockSettlement = mock(Settlement.class);
		SettlementAccountRequest request = mock(SettlementAccountRequest.class);

		when(settlementRepository.getById(eq(groupId))).thenReturn(mockSettlement);

		// when
		Settlement updatedSettlement = settlementUpdater.updateAccount(request, groupId);

		// then
		verify(mockSettlement, times(1)).updateAccount(any());
		assertThat(updatedSettlement).isEqualTo(mockSettlement);
	}

	@DisplayName("그룹이 존재하지 않으면 계좌 정보를 수정할 때 예외가 발생한다.")
	@Test
	void updateAccountNotFoundGroup() {
		// given
		Long groupId = 1L;
		SettlementAccountRequest request = mock(SettlementAccountRequest.class);

		doThrow(new GroupNotFoundException(groupId)).when(settlementRepository).getById(groupId);

		// when & then
		assertThatThrownBy(() -> settlementUpdater.updateAccount(request, groupId))
			.isInstanceOf(GroupNotFoundException.class);
	}

}
