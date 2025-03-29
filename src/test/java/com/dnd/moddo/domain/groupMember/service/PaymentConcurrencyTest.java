package com.dnd.moddo.domain.groupMember.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDateTime;
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
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
import com.dnd.moddo.domain.groupMember.dto.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberReader;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberUpdater;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberValidator;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ModdoApplication.class) // 명시적으로 설정 클래스를 지정
public class PaymentConcurrencyTest {
	@Autowired
	private GroupMemberUpdater groupMemberUpdater;
	@Autowired
	private GroupMemberRepository groupMemberRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private GroupMemberValidator groupMemberValidator;
	@Autowired
	private GroupMemberReader groupMemberReader;
	@Autowired
	private GroupReader groupReader;

	private GroupMember groupMember;

	@BeforeEach
	void setUp() {
		Group mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now().plusMinutes(1),
			"은행", "계좌", LocalDateTime.now().plusDays(1));

		groupRepository.save(mockGroup);

		groupMember = groupMemberRepository.save(
			GroupMember.builder()
				.name("김반숙")
				.group(mockGroup)
				.profileId(1)
				.role(ExpenseRole.PARTICIPANT)
				.build());
	}

	@DisplayName("낙관적 락을 적용했을 때 업데이트 충돌로 일부 요청에서 예외가 발생한다.")
	@Test
	void optimisticLock_shouldThrowExceptionOnConflict() throws InterruptedException {
		//given
		Long groupMemberId = groupMember.getId();
		int threadCount = 10;
		CountDownLatch latch = new CountDownLatch(threadCount);

		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger failureCount = new AtomicInteger();
		//when

		for (int i = 0; i < threadCount; i++) {
			new Thread(() -> {
				try {
					groupMemberUpdater.updatePaymentStatus(groupMemberId, new PaymentStatusUpdateRequest(true));
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

		GroupMember result = groupMemberRepository.getById(groupMemberId);

		assertThat(result.isPaid()).isTrue();
		assertThat(successCount.get()).isGreaterThan(0);
		assertThat(failureCount.get()).isGreaterThan(0);
	}
}
