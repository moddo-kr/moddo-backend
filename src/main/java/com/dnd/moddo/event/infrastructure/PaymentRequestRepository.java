package com.dnd.moddo.event.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.moddo.event.domain.paymentRequest.PaymentRequest;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus;
import com.dnd.moddo.event.domain.paymentRequest.exception.PaymentRequestNotFoundException;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {

	@Query("""
		select count(pr) > 0
		from PaymentRequest pr
		where pr.settlement.id = :settlementId
		  and pr.requestMember.id = :requestMemberId
		  and pr.status = :status
		""")
	boolean existsBySettlementIdAndRequestMemberIdAndStatus(
		@Param("settlementId") Long settlementId,
		@Param("requestMemberId") Long requestMemberId,
		@Param("status") PaymentRequestStatus status
	);

	default PaymentRequest getById(Long paymentRequestId) {
		return findById(paymentRequestId)
			.orElseThrow(() -> new PaymentRequestNotFoundException(paymentRequestId));
	}

	List<PaymentRequest> findByTargetUserId(Long targetUserId);
}
