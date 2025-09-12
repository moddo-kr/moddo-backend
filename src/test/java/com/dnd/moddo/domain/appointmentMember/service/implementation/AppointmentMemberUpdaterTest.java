package com.dnd.moddo.domain.appointmentMember.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.appointmentMember.dto.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.domain.appointmentMember.dto.request.appointmentMemberSaveRequest;
import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.appointmentMember.exception.AppointmentMemberDuplicateNameException;
import com.dnd.moddo.domain.appointmentMember.repository.AppointmentMemberRepository;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.settlement.service.implementation.SettlementReader;
import com.dnd.moddo.global.config.S3Bucket;

@ExtendWith(MockitoExtension.class)
class AppointmentMemberUpdaterTest {
	@Mock
	private AppointmentMemberRepository appointmentMemberRepository;
	@Mock
	private AppointmentMemberReader appointmentMemberReader;
	@Mock
	private AppointmentMemberValidator appointmentMemberValidator;
	@Mock
	private SettlementReader settlementReader;
	@Mock
	private S3Bucket s3Bucket;
	@InjectMocks
	private AppointmentMemberUpdater appointmentMemberUpdater;

	private Settlement mockSettlement;

	@BeforeEach
	void setup() {
		mockSettlement = mock(Settlement.class);
	}

	@DisplayName("추가하려는 참여자의 이름이 기존 참여자의 이름과 중복되지 않을 경우 참여자 추가에 성공한다.")
	@Test
	void addToSettlementSuccess() {
		// given
		Long groupId = 1L;
		appointmentMemberSaveRequest request = mock(appointmentMemberSaveRequest.class);
		String newMemberName = "김반숙";

		when(request.name()).thenReturn(newMemberName);
		when(settlementReader.read(eq(groupId))).thenReturn(mockSettlement);

		List<AppointmentMember> mockAppointmentMembers = new ArrayList<>();
		when(appointmentMemberReader.findAllBySettlementId(eq(groupId))).thenReturn(mockAppointmentMembers);

		doNothing().when(appointmentMemberValidator).validateMemberNamesNotDuplicate(any());

		AppointmentMember expectedAppointmentMember = AppointmentMember.builder()
			.name(newMemberName)
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.profileId(1)
			.build();
		when(appointmentMemberRepository.save(any())).thenReturn(expectedAppointmentMember);

		// when
		AppointmentMember result = appointmentMemberUpdater.addToSettlement(groupId, request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getSettlement()).isEqualTo(mockSettlement);
		assertThat(result.getName()).isEqualTo(newMemberName);
		assertThat(result.getProfileId()).isEqualTo(1);

		verify(appointmentMemberRepository, times(1)).save(any());
	}

	@DisplayName("추가하려는 참여자의 이름이 기존 참여자의 이름과 중복되는 경우 예외가 발생한다.")
	@Test
	void addToSettlementDuplicatedName() {
		// given
		Long groupId = 1L;
		appointmentMemberSaveRequest request = mock(appointmentMemberSaveRequest.class);
		String duplicatedName = "김반숙";

		when(request.name()).thenReturn(duplicatedName);
		when(settlementReader.read(eq(groupId))).thenReturn(mockSettlement);

		List<AppointmentMember> mockAppointmentMembers = new ArrayList<>();
		AppointmentMember existingMember = AppointmentMember.builder().name(duplicatedName).build();
		mockAppointmentMembers.add(existingMember);
		when(appointmentMemberReader.findAllBySettlementId(eq(groupId))).thenReturn(mockAppointmentMembers);

		doThrow(new AppointmentMemberDuplicateNameException()).when(appointmentMemberValidator)
			.validateMemberNamesNotDuplicate(any());

		// when & then
		assertThatThrownBy(() -> {
			appointmentMemberUpdater.addToSettlement(groupId, request);
		}).hasMessage("중복된 참여자의 이름은 저장할 수 없습니다.");
	}

	@DisplayName("참여자가 유효할 때 참여자의 입금 상태를 변경할 수 있다.")
	@Test
	void updatePaymentStatus_Success() {
		// given
		AppointmentMember appointmentMember = AppointmentMember.builder()
			.name("김반숙")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.build();
		PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest(true);

		when(appointmentMemberRepository.getById(any())).thenReturn(appointmentMember);

		// when
		AppointmentMember result = appointmentMemberUpdater.updatePaymentStatus(1L, request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.isPaid()).isTrue();
	}

	@DisplayName("9번째 이상의 참여자가 추가될 때 프로필 ID가 올바르게 순환된다.")
	@Test
	void addToSettlementProfileRotationSuccess() {
		// given
		Long groupId = 1L;
		appointmentMemberSaveRequest request = mock(appointmentMemberSaveRequest.class);
		String newMemberName = "김철수";

		when(request.name()).thenReturn(newMemberName);
		when(settlementReader.read(eq(groupId))).thenReturn(mockSettlement);

		// 기존 멤버 8명 설정
		List<AppointmentMember> mockAppointmentMembers = new ArrayList<>();
		for (int i = 1; i <= 8; i++) {
			mockAppointmentMembers.add(
				AppointmentMember.builder()
					.name("멤버" + i)
					.settlement(mockSettlement)
					.profileId(i)
					.role(ExpenseRole.PARTICIPANT)
					.build()
			);
		}
		when(appointmentMemberReader.findAllBySettlementId(eq(groupId))).thenReturn(mockAppointmentMembers);

		doNothing().when(appointmentMemberValidator).validateMemberNamesNotDuplicate(any());

		AppointmentMember expectedAppointmentMember = AppointmentMember.builder()
			.name(newMemberName)
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.profileId(1)
			.build();
		when(appointmentMemberRepository.save(any())).thenReturn(expectedAppointmentMember);

		// when
		AppointmentMember result = appointmentMemberUpdater.addToSettlement(groupId, request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getSettlement()).isEqualTo(mockSettlement);
		assertThat(result.getName()).isEqualTo(newMemberName);
		assertThat(result.getProfileId()).isEqualTo(1);

		verify(appointmentMemberRepository, times(1)).save(any());
	}
}
