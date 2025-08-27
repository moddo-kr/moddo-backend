package com.dnd.moddo.domain.appointmentMember.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.appointmentMember.dto.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.domain.appointmentMember.dto.request.appointmentMemberSaveRequest;
import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.appointmentMember.exception.PaymentConcurrencyException;
import com.dnd.moddo.domain.appointmentMember.repository.AppointmentMemberRepository;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.settlement.service.implementation.SettlementReader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentMemberUpdater {
	private final AppointmentMemberRepository appointmentMemberRepository;
	private final AppointmentMemberReader appointmentMemberReader;
	private final AppointmentMemberValidator appointmentMemberValidator;
	private final SettlementReader settlementReader;

	@Transactional
	public AppointmentMember addToGroup(Long groupId, appointmentMemberSaveRequest request) {
		Settlement settlement = settlementReader.read(groupId);
		List<AppointmentMember> appointmentMembers = appointmentMemberReader.findAllByGroupId(groupId);

		List<String> existingNames = new ArrayList<>(
			appointmentMembers.stream().map(AppointmentMember::getName).toList());
		existingNames.add(request.name());

		appointmentMemberValidator.validateMemberNamesNotDuplicate(existingNames);

		List<Integer> usedProfiles = appointmentMembers.stream()
			.filter(member -> !member.isManager())
			.map(AppointmentMember::getProfileId)
			.toList();

		Integer newProfileId = findAvailableProfileId(usedProfiles);

		AppointmentMember newMember = request.toEntity(settlement, newProfileId, ExpenseRole.PARTICIPANT);
		newMember = appointmentMemberRepository.save(newMember);

		return newMember;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public AppointmentMember updatePaymentStatus(Long appointmentMemberId, PaymentStatusUpdateRequest request) {
		try {
			AppointmentMember appointmentMember = appointmentMemberRepository.getById(appointmentMemberId);
			if (appointmentMember.isPaid() != request.isPaid()) {
				appointmentMember.updatePaymentStatus(request.isPaid());
				appointmentMemberRepository.save(appointmentMember);
			}
			return appointmentMember;
		} catch (OptimisticLockingFailureException e) {
			throw new PaymentConcurrencyException();
		}
	}

	private Integer findAvailableProfileId(List<Integer> usedProfiles) {
		for (int i = 1; i <= 8; i++) {
			if (!usedProfiles.contains(i)) {
				return i;
			}
		}

		return (usedProfiles.size() % 8) + 1;
	}
}



