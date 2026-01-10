package com.dnd.moddo.domain.Member.service;

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
import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.application.impl.MemberUpdater;
import com.dnd.moddo.event.application.impl.MemberValidator;
import com.dnd.moddo.event.application.impl.SettlementReader;
import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.infrastructure.MemberRepository;
import com.dnd.moddo.event.infrastructure.SettlementRepository;
import com.dnd.moddo.event.presentation.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ModdoApplication.class) // 명시적으로 설정 클래스를 지정
public class PaymentConcurrencyTest {
	@Autowired
	private MemberUpdater memberUpdater;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private SettlementRepository settlementRepository;
	@Autowired
	private MemberValidator memberValidator;
	@Autowired
	private MemberReader memberReader;
	@Autowired
	private SettlementReader settlementReader;

	private Member member;

	@BeforeEach
	void setUp() {
		Settlement mockSettlement = GroupTestFactory.createDefault();

		settlementRepository.save(mockSettlement);

		member = memberRepository.save(
			Member.builder()
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
		Long groupMemberId = member.getId();
		int threadCount = 10;
		CountDownLatch latch = new CountDownLatch(threadCount);

		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger failureCount = new AtomicInteger();
		//when

		for (int i = 0; i < threadCount; i++) {
			new Thread(() -> {
				try {
					memberUpdater.updatePaymentStatus(groupMemberId, new PaymentStatusUpdateRequest(true));
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

		Member result = memberRepository.getById(groupMemberId);

		assertThat(result.isPaid()).isTrue();
		assertThat(successCount.get()).isGreaterThan(0);
		assertThat(failureCount.get()).isGreaterThan(0);
	}
}
