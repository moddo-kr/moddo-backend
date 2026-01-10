package com.dnd.moddo.event.application.impl;

import org.springframework.stereotype.Service;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.member.exception.ManagerCannotDeleteException;
import com.dnd.moddo.event.infrastructure.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberDeleter {
	private final MemberRepository memberRepository;
	private final MemberReader memberReader;

	public void delete(Long appointmentMemberId) {
		Member member = memberReader.findByAppointmentMemberId(appointmentMemberId);
		if (member.isManager()) {
			throw new ManagerCannotDeleteException(appointmentMemberId);
		}
		memberRepository.delete(member);
	}
}
