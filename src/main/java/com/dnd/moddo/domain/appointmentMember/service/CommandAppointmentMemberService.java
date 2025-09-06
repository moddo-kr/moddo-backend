package com.dnd.moddo.domain.appointmentMember.service;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.appointmentMember.dto.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.domain.appointmentMember.dto.request.appointmentMemberSaveRequest;
import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMemberResponse;
import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.service.implementation.AppointmentMemberCreator;
import com.dnd.moddo.domain.appointmentMember.service.implementation.AppointmentMemberDeleter;
import com.dnd.moddo.domain.appointmentMember.service.implementation.AppointmentMemberUpdater;
import com.dnd.moddo.domain.settlement.entity.Settlement;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandAppointmentMemberService {
	private final AppointmentMemberCreator appointmentMemberCreator;
	private final AppointmentMemberUpdater appointmentMemberUpdater;
	private final AppointmentMemberDeleter appointmentMemberDeleter;

	public AppointmentMemberResponse createManager(Settlement settlement, Long userId) {
		AppointmentMember appointmentMember = appointmentMemberCreator.createManagerForSettlement(settlement, userId);
		return AppointmentMemberResponse.of(appointmentMember);
	}

	public AppointmentMemberResponse addAppointmentMember(Long settlementId, appointmentMemberSaveRequest request) {
		AppointmentMember appointmentMember = appointmentMemberUpdater.addToSettlement(settlementId, request);
		return AppointmentMemberResponse.of(appointmentMember);
	}

	public AppointmentMemberResponse updatePaymentStatus(Long appointmentMemberId, PaymentStatusUpdateRequest request) {
		AppointmentMember appointmentMember = appointmentMemberUpdater.updatePaymentStatus(appointmentMemberId,
			request);
		return AppointmentMemberResponse.of(appointmentMember);
	}

	public void delete(Long appointmentMemberId) {
		appointmentMemberDeleter.delete(appointmentMemberId);
	}

}
