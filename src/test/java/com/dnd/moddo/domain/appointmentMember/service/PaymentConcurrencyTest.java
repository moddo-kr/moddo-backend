package com.dnd.moddo.domain.appointmentMember.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dnd.moddo.ModdoApplication;
import com.dnd.moddo.domain.appointmentMember.dto.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.appointmentMember.repository.AppointmentMemberRepository;
import com.dnd.moddo.domain.appointmentMember.service.implementation.AppointmentMemberReader;
import com.dnd.moddo.domain.appointmentMember.service.implementation.AppointmentMemberUpdater;
import com.dnd.moddo.domain.appointmentMember.service.implementation.AppointmentMemberValidator;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.settlement.repository.SettlementRepository;
import com.dnd.moddo.domain.settlement.service.implementation.SettlementReader;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ModdoApplication.class) // 명시적으로 설정 클래스를 지정
public class PaymentConcurrencyTest {
	@Autowired
	private AppointmentMemberUpdater appointmentMemberUpdater;
	@Autowired
	private AppointmentMemberRepository appointmentMemberRepository;
	@Autowired
	private SettlementRepository settlementRepository;
	@Autowired
	private AppointmentMemberValidator appointmentMemberValidator;
	@Autowired
	private AppointmentMemberReader appointmentMemberReader;
	@Autowired
	private SettlementReader settlementReader;

	private AppointmentMember appointmentMember;

	@BeforeEach
	void setUp() {
		Settlement mockSettlement = GroupTestFactory.createDefault();

		settlementRepository.save(mockSettlement);

		appointmentMember = appointmentMemberRepository.save(
			AppointmentMember.builder()
				.name("김반숙")
				.settlement(mockSettlement)
				.profileId(1)
				.role(ExpenseRole.PARTICIPANT)
				.build());
	}

	@DisplayName("낙관적 락을 적용했을 때 업데이트 충돌로 일부 요청에서 예외가 발생한다.")
	@Test
	void optimisticLock_shouldThrowExceptionOnConflict() throws InterruptedException {
		//given
		Long groupMemberId = appointmentMember.getId();
		int threadCount = 10;
		CountDownLatch latch = new CountDownLatch(threadCount);

		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger failureCount = new AtomicInteger();
		//when

		for (int i = 0; i < threadCount; i++) {
			new Thread(() -> {
				try {
					appointmentMemberUpdater.updatePaymentStatus(groupMemberId, new PaymentStatusUpdateRequest(true));
					successCount.incrementAndGet();
				} catch (OptimisticLockingFailureException e) {
					failureCount.incrementAndGet(); // 동시 수정 충돌 발생
				} finally {
					latch.countDown();
				}
			}).start();
		}

		latch.await();

		//then

		AppointmentMember result = appointmentMemberRepository.getById(groupMemberId);

		assertThat(result.isPaid()).isTrue();
		assertThat(successCount.get()).isGreaterThan(0);
		assertThat(failureCount.get()).isGreaterThan(0);
	}
}
