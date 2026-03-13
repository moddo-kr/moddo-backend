package com.dnd.moddo.domain.Member.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import com.dnd.moddo.common.config.S3Bucket;
import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.application.impl.MemberUpdater;
import com.dnd.moddo.event.application.impl.MemberValidator;
import com.dnd.moddo.event.application.impl.SettlementReader;
import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.member.exception.InvalidMemberException;
import com.dnd.moddo.event.domain.member.exception.MemberAlreadyAssignedException;
import com.dnd.moddo.event.domain.member.exception.MemberDuplicateNameException;
import com.dnd.moddo.event.domain.member.exception.MemberNotAssignedException;
import com.dnd.moddo.event.domain.member.exception.MemberSelectionNotAllowedException;
import com.dnd.moddo.event.domain.member.exception.MemberSelectionUnauthorizedException;
import com.dnd.moddo.event.domain.member.exception.PaymentConcurrencyException;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.infrastructure.MemberRepository;
import com.dnd.moddo.event.presentation.request.MemberSaveRequest;
import com.dnd.moddo.event.presentation.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.global.support.UserTestFactory;
import com.dnd.moddo.user.domain.User;
import com.dnd.moddo.user.infrastructure.UserRepository;

@ExtendWith(MockitoExtension.class)
class MemberUpdaterTest {
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private MemberReader memberReader;
	@Mock
	private MemberValidator memberValidator;
	@Mock
	private SettlementReader settlementReader;
	@Mock
	private S3Bucket s3Bucket;
	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private MemberUpdater memberUpdater;

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
		MemberSaveRequest request = mock(MemberSaveRequest.class);
		String newMemberName = "김반숙";

		when(request.name()).thenReturn(newMemberName);
		when(settlementReader.read(eq(groupId))).thenReturn(mockSettlement);

		List<Member> mockMembers = new ArrayList<>();
		when(memberReader.findAllBySettlementId(eq(groupId))).thenReturn(mockMembers);

		doNothing().when(memberValidator).validateMemberNamesNotDuplicate(any());

		Member expectedMember = Member.builder()
			.name(newMemberName)
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.profileId(1)
			.build();
		when(memberRepository.save(any())).thenReturn(expectedMember);

		// when
		Member result = memberUpdater.addToSettlement(groupId, request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getSettlement()).isEqualTo(mockSettlement);
		assertThat(result.getName()).isEqualTo(newMemberName);
		assertThat(result.getProfileId()).isEqualTo(1);

		verify(memberRepository, times(1)).save(any());
	}

	@DisplayName("추가하려는 참여자의 이름이 기존 참여자의 이름과 중복되는 경우 예외가 발생한다.")
	@Test
	void addToSettlementDuplicatedName() {
		// given
		Long groupId = 1L;
		MemberSaveRequest request = mock(MemberSaveRequest.class);
		String duplicatedName = "김반숙";

		when(request.name()).thenReturn(duplicatedName);
		when(settlementReader.read(eq(groupId))).thenReturn(mockSettlement);

		List<Member> mockMembers = new ArrayList<>();
		Member existingMember = Member.builder().name(duplicatedName).build();
		mockMembers.add(existingMember);
		when(memberReader.findAllBySettlementId(eq(groupId))).thenReturn(mockMembers);

		doThrow(new MemberDuplicateNameException()).when(memberValidator)
			.validateMemberNamesNotDuplicate(any());

		// when & then
		assertThatThrownBy(() -> {
			memberUpdater.addToSettlement(groupId, request);
		}).hasMessage("중복된 참여자의 이름은 저장할 수 없습니다.");
	}

	@DisplayName("참여자가 유효할 때 참여자의 입금 상태를 변경할 수 있다.")
	@Test
	void updatePaymentStatus_Success() {
		// given
		Member member = Member.builder()
			.name("김반숙")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.build();
		PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest(true);

		when(memberRepository.getById(any())).thenReturn(member);

		// when
		Member result = memberUpdater.updatePaymentStatus(1L, request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.isPaid()).isTrue();
	}

	@DisplayName("입금 상태 변경 중 낙관적 락 충돌이 발생하면 예외가 발생한다.")
	@Test
	void updatePaymentStatusFailWhenConcurrencyIssue() {
		PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest(true);

		when(memberRepository.getById(any())).thenThrow(new OptimisticLockingFailureException("conflict"));

		assertThatThrownBy(() -> memberUpdater.updatePaymentStatus(1L, request))
			.isInstanceOf(PaymentConcurrencyException.class);
	}

	@DisplayName("9번째 이상의 참여자가 추가될 때 프로필 ID가 올바르게 순환된다.")
	@Test
	void addToSettlementProfileRotationSuccess() {
		// given
		Long groupId = 1L;
		MemberSaveRequest request = mock(MemberSaveRequest.class);
		String newMemberName = "김철수";

		when(request.name()).thenReturn(newMemberName);
		when(settlementReader.read(eq(groupId))).thenReturn(mockSettlement);

		// 기존 멤버 8명 설정
		List<Member> mockMembers = new ArrayList<>();
		for (int i = 1; i <= 8; i++) {
			mockMembers.add(
				Member.builder()
					.name("멤버" + i)
					.settlement(mockSettlement)
					.profileId(i)
					.role(ExpenseRole.PARTICIPANT)
					.build()
			);
		}
		when(memberReader.findAllBySettlementId(eq(groupId))).thenReturn(mockMembers);

		doNothing().when(memberValidator).validateMemberNamesNotDuplicate(any());

		Member expectedMember = Member.builder()
			.name(newMemberName)
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.profileId(1)
			.build();
		when(memberRepository.save(any())).thenReturn(expectedMember);

		// when
		Member result = memberUpdater.addToSettlement(groupId, request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getSettlement()).isEqualTo(mockSettlement);
		assertThat(result.getName()).isEqualTo(newMemberName);
		assertThat(result.getProfileId()).isEqualTo(1);

		verify(memberRepository, times(1)).save(any());
	}

	@DisplayName("로그인 사용자가 선택되지 않은 참여자를 선택할 수 있다.")
	@Test
	void assignMemberSuccess() {
		Long settlementId = 1L;
		Long memberId = 2L;
		Long userId = 3L;

		User user = UserTestFactory.createWithEmail("assign@test.com");
		Member member = mock(Member.class);

		when(memberRepository.getById(memberId)).thenReturn(member);
		when(member.isInSettlement(settlementId)).thenReturn(true);
		when(member.isManager()).thenReturn(false);
		when(member.isAssigned()).thenReturn(false);
		when(memberRepository.findBySettlementIdAndUserId(settlementId, userId)).thenReturn(Optional.empty());
		when(userRepository.getById(userId)).thenReturn(user);
		when(memberRepository.save(member)).thenReturn(member);

		Member result = memberUpdater.assignMember(settlementId, memberId, userId);

		assertThat(result).isEqualTo(member);
		verify(member).assignUser(user);
		verify(memberRepository).findBySettlementIdAndUserId(settlementId, userId);
		verify(memberRepository).save(member);
	}

	@DisplayName("다른 정산의 참여자를 선택하면 예외가 발생한다.")
	@Test
	void assignMemberFailWhenInvalidSettlement() {
		Long settlementId = 1L;
		Long memberId = 2L;
		Long userId = 3L;
		Member member = mock(Member.class);

		when(memberRepository.getById(memberId)).thenReturn(member);
		when(member.isInSettlement(settlementId)).thenReturn(false);
		when(member.getId()).thenReturn(memberId);

		assertThatThrownBy(() -> memberUpdater.assignMember(settlementId, memberId, userId))
			.isInstanceOf(InvalidMemberException.class);
	}

	@DisplayName("총무는 선택할 수 없다.")
	@Test
	void assignMemberFailWhenManager() {
		Long settlementId = 1L;
		Long memberId = 2L;
		Long userId = 3L;
		Member member = mock(Member.class);

		when(memberRepository.getById(memberId)).thenReturn(member);
		when(member.isInSettlement(settlementId)).thenReturn(true);
		when(member.isManager()).thenReturn(true);
		when(member.getId()).thenReturn(memberId);

		assertThatThrownBy(() -> memberUpdater.assignMember(settlementId, memberId, userId))
			.isInstanceOf(MemberSelectionNotAllowedException.class);
	}

	@DisplayName("기존에 선택한 참여자가 있으면 자동으로 해제하고 새 참여자로 교체한다.")
	@Test
	void assignMemberReplaceExistingSelection() {
		Long settlementId = 1L;
		Long memberId = 2L;
		Long userId = 3L;
		User user = UserTestFactory.createWithEmail("assign@test.com");
		Member member = mock(Member.class);
		Member assignedMember = mock(Member.class);

		when(memberRepository.getById(memberId)).thenReturn(member);
		when(member.isInSettlement(settlementId)).thenReturn(true);
		when(member.isManager()).thenReturn(false);
		when(member.isAssigned()).thenReturn(false);
		when(memberRepository.findBySettlementIdAndUserId(settlementId, userId))
			.thenReturn(Optional.of(assignedMember));
		when(userRepository.getById(userId)).thenReturn(user);
		when(memberRepository.save(member)).thenReturn(member);

		Member result = memberUpdater.assignMember(settlementId, memberId, userId);

		assertThat(result).isEqualTo(member);
		verify(assignedMember).unassignUser(userId);
		verify(member).assignUser(user);
		verify(memberRepository).findBySettlementIdAndUserId(settlementId, userId);
		verify(memberRepository).save(member);
	}

	@DisplayName("이미 본인이 선택한 참여자를 다시 선택하면 그대로 반환한다.")
	@Test
	void assignMemberWhenAlreadyAssignedToMe() {
		Long settlementId = 1L;
		Long memberId = 2L;
		Long userId = 3L;
		Member member = mock(Member.class);

		when(memberRepository.getById(memberId)).thenReturn(member);
		when(member.isInSettlement(settlementId)).thenReturn(true);
		when(member.isManager()).thenReturn(false);
		when(member.isAssigned()).thenReturn(true);
		when(member.isAssignedTo(userId)).thenReturn(true);

		Member result = memberUpdater.assignMember(settlementId, memberId, userId);

		assertThat(result).isEqualTo(member);
		verify(memberRepository, never()).findBySettlementIdAndUserId(anyLong(), anyLong());
		verify(memberRepository, never()).save(any());
		verify(member, never()).assignUser(any());
	}

	@DisplayName("이미 다른 사용자가 선택한 참여자를 선택하면 예외가 발생한다.")
	@Test
	void assignMemberFailWhenMemberAlreadyAssignedToAnotherUser() {
		Long settlementId = 1L;
		Long memberId = 2L;
		Long userId = 3L;
		Member member = mock(Member.class);

		when(memberRepository.getById(memberId)).thenReturn(member);
		when(member.isInSettlement(settlementId)).thenReturn(true);
		when(member.isManager()).thenReturn(false);
		when(member.isAssigned()).thenReturn(true);
		when(member.isAssignedTo(userId)).thenReturn(false);

		assertThatThrownBy(() -> memberUpdater.assignMember(settlementId, memberId, userId))
			.isInstanceOf(MemberAlreadyAssignedException.class);

		verify(memberRepository, never()).findBySettlementIdAndUserId(anyLong(), anyLong());
		verify(memberRepository, never()).save(any());
	}

	@DisplayName("로그인 사용자가 본인이 선택한 참여자를 해제할 수 있다.")
	@Test
	void unassignMemberSuccess() {
		Long settlementId = 1L;
		Long memberId = 2L;
		Long userId = 3L;

		Member member = mock(Member.class);

		when(memberRepository.getById(memberId)).thenReturn(member);
		when(member.isInSettlement(settlementId)).thenReturn(true);
		when(member.isManager()).thenReturn(false);
		when(member.isAssigned()).thenReturn(true);
		when(member.isAssignedTo(userId)).thenReturn(true);
		when(memberRepository.save(member)).thenReturn(member);

		Member result = memberUpdater.unassignMember(settlementId, memberId, userId);

		assertThat(result).isEqualTo(member);
		verify(member).unassignUser(userId);
		verify(memberRepository).save(member);
	}

	@DisplayName("다른 정산의 참여자를 해제하면 예외가 발생한다.")
	@Test
	void unassignMemberFailWhenInvalidSettlement() {
		Long settlementId = 1L;
		Long memberId = 2L;
		Long userId = 3L;
		Member member = mock(Member.class);

		when(memberRepository.getById(memberId)).thenReturn(member);
		when(member.isInSettlement(settlementId)).thenReturn(false);
		when(member.getId()).thenReturn(memberId);

		assertThatThrownBy(() -> memberUpdater.unassignMember(settlementId, memberId, userId))
			.isInstanceOf(InvalidMemberException.class);
	}

	@DisplayName("총무는 선택 해제할 수 없다.")
	@Test
	void unassignMemberFailWhenManager() {
		Long settlementId = 1L;
		Long memberId = 2L;
		Long userId = 3L;
		Member member = mock(Member.class);

		when(memberRepository.getById(memberId)).thenReturn(member);
		when(member.isInSettlement(settlementId)).thenReturn(true);
		when(member.isManager()).thenReturn(true);
		when(member.getId()).thenReturn(memberId);

		assertThatThrownBy(() -> memberUpdater.unassignMember(settlementId, memberId, userId))
			.isInstanceOf(MemberSelectionNotAllowedException.class);
	}

	@DisplayName("선택되지 않은 참여자를 해제하면 예외가 발생한다.")
	@Test
	void unassignMemberFailWhenNotAssigned() {
		Long settlementId = 1L;
		Long memberId = 2L;
		Long userId = 3L;
		Member member = mock(Member.class);

		when(memberRepository.getById(memberId)).thenReturn(member);
		when(member.isInSettlement(settlementId)).thenReturn(true);
		when(member.isManager()).thenReturn(false);
		when(member.isAssigned()).thenReturn(false);

		assertThatThrownBy(() -> memberUpdater.unassignMember(settlementId, memberId, userId))
			.isInstanceOf(MemberNotAssignedException.class);
	}

	@DisplayName("본인이 선택하지 않은 참여자를 해제하면 예외가 발생한다.")
	@Test
	void unassignMemberFailWhenUnauthorized() {
		Long settlementId = 1L;
		Long memberId = 2L;
		Long userId = 3L;
		Member member = mock(Member.class);

		when(memberRepository.getById(memberId)).thenReturn(member);
		when(member.isInSettlement(settlementId)).thenReturn(true);
		when(member.isManager()).thenReturn(false);
		when(member.isAssigned()).thenReturn(true);
		when(member.isAssignedTo(userId)).thenReturn(false);

		assertThatThrownBy(() -> memberUpdater.unassignMember(settlementId, memberId, userId))
			.isInstanceOf(MemberSelectionUnauthorizedException.class);
	}
}
