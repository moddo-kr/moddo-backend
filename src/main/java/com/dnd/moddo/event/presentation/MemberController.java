package com.dnd.moddo.event.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.auth.infrastructure.security.LoginUser;
import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.common.support.VerifyManagerPermission;
import com.dnd.moddo.event.application.command.CommandMemberService;
import com.dnd.moddo.event.application.query.QueryMemberService;
import com.dnd.moddo.event.application.query.QuerySettlementService;
import com.dnd.moddo.event.domain.member.type.MemberSortType;
import com.dnd.moddo.event.presentation.request.MemberSaveRequest;
import com.dnd.moddo.event.presentation.request.MemberSelectionRequest;
import com.dnd.moddo.event.presentation.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.event.presentation.response.MemberResponse;
import com.dnd.moddo.event.presentation.response.MembersResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/groups/{code}/members")
public class MemberController {

	private final QueryMemberService queryMemberService;
	private final CommandMemberService commandMemberService;
	private final QuerySettlementService querySettlementService;

	@GetMapping
	public ResponseEntity<MembersResponse> getMembers(
		@PathVariable String code,
		@RequestParam(defaultValue = "CREATED") String sortType
	) {
		Long settlementId = querySettlementService.findIdByCode(code);
		MembersResponse response = queryMemberService.findAll(settlementId, MemberSortType.from(sortType));
		return ResponseEntity.ok(response);
	}

	@VerifyManagerPermission
	@PostMapping
	public ResponseEntity<MemberResponse> addMember(
		@PathVariable String code,
		@Valid @RequestBody MemberSaveRequest request
	) {
		Long settlementId = querySettlementService.findIdByCode(code);
		MemberResponse response =
			commandMemberService.addMember(settlementId, request);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{memberId}")
	public ResponseEntity<MemberResponse> updatePaymentStatus(
		@PathVariable String code,
		@PathVariable Long memberId,
		@RequestBody PaymentStatusUpdateRequest request
	) {
		MemberResponse response =
			commandMemberService.updatePaymentStatus(memberId, request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/assign")
	public ResponseEntity<MemberResponse> assignMember(
		@PathVariable String code,
		@Valid @RequestBody MemberSelectionRequest request,
		@LoginUser LoginUserInfo loginUser
	) {
		Long settlementId = querySettlementService.findIdByCode(code);
		MemberResponse response = commandMemberService.assignMember(settlementId, request.memberId(), loginUser.userId());
		return ResponseEntity.ok(response);
	}

	@PostMapping("/unassign")
	public ResponseEntity<MemberResponse> unassignMember(
		@PathVariable String code,
		@Valid @RequestBody MemberSelectionRequest request,
		@LoginUser LoginUserInfo loginUser
	) {
		Long settlementId = querySettlementService.findIdByCode(code);
		MemberResponse response = commandMemberService.unassignMember(settlementId, request.memberId(), loginUser.userId());
		return ResponseEntity.ok(response);
	}

	@VerifyManagerPermission
	@DeleteMapping("/{memberId}")
	public ResponseEntity<Void> deleteMember(
		@PathVariable String code,
		@PathVariable Long memberId
	) {
		commandMemberService.delete(memberId);
		return ResponseEntity.noContent().build();
	}
}
