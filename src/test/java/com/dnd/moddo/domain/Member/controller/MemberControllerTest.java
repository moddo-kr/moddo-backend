package com.dnd.moddo.domain.Member.controller;

import static com.dnd.moddo.event.domain.member.ExpenseRole.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.event.domain.member.type.MemberSortType;
import com.dnd.moddo.event.presentation.request.MemberSaveRequest;
import com.dnd.moddo.event.presentation.request.MemberSelectionRequest;
import com.dnd.moddo.event.presentation.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.event.presentation.response.MemberResponse;
import com.dnd.moddo.event.presentation.response.MembersResponse;
import com.dnd.moddo.global.util.RestDocsTestSupport;

public class MemberControllerTest extends RestDocsTestSupport {

	@Test
	@DisplayName("모임원을 성공적으로 조회한다.")
	void getAppointmentMembers() throws Exception {
		// given
		String code = "code";
		Long groupId = 1L;

		MembersResponse mockResponse = new MembersResponse(List.of(
			new MemberResponse(
				1L,
				MANAGER,
				"김모또",
				"https://moddo-s3.s3.amazonaws.com/profile/MODDO.png",
				10L,
				true,
				LocalDateTime.of(2026, 3, 13, 21, 30)
			),
			new MemberResponse(
				2L,
				PARTICIPANT,
				"김반숙",
				"https://moddo-s3.s3.amazonaws.com/profile/1.png",
				null,
				false,
				null
			)
		));

		when(querySettlementService.findIdByCode(code)).thenReturn(groupId);
		when(queryMemberService.findAll(groupId, MemberSortType.CREATED)).thenReturn(mockResponse);

		// when & then
		mockMvc.perform(get("/api/v1/groups/{code}/members", code)
				.param("sortType", "CREATED"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.members").isArray())
			.andExpect(jsonPath("$.members[0].id").value(1L))
			.andExpect(jsonPath("$.members[0].userId").value(10L))
			.andExpect(jsonPath("$.members[1].id").value(2L))
			.andExpect(jsonPath("$.members[1].userId").doesNotExist())
			.andDo(restDocs.document(
				pathParameters(
					parameterWithName("code").description("정산 코드")
				),
				queryParameters(
					parameterWithName("sortType").description("정렬 기준 (CREATED | NAME | PAID_AT)").optional()
				),
				responseFields(
					fieldWithPath("members").type(JsonFieldType.ARRAY).description("모임원 목록"),
					fieldWithPath("members[].id").type(JsonFieldType.NUMBER).description("모임원 ID"),
					fieldWithPath("members[].role").type(JsonFieldType.STRING).description("모임원 역할"),
					fieldWithPath("members[].name").type(JsonFieldType.STRING).description("모임원 이름"),
					fieldWithPath("members[].profile").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
					fieldWithPath("members[].userId").type(JsonFieldType.NUMBER)
						.description("연결된 사용자 ID, 아직 선택되지 않은 참여자는 null")
						.optional(),
					fieldWithPath("members[].isPaid").type(JsonFieldType.BOOLEAN).description("정산 완료 여부"),
					fieldWithPath("members[].paidAt").type(JsonFieldType.STRING).description("정산 완료 시각").optional()
				)
			));

		verify(querySettlementService).findIdByCode(code);
		verify(queryMemberService).findAll(groupId, MemberSortType.CREATED);
	}

	@Test
	@DisplayName("유효하지 않은 sortType으로 모임원을 조회하면 400을 반환한다.")
	void getAppointmentMembers_whenInvalidSortType() throws Exception {
		mockMvc.perform(get("/api/v1/groups/{code}/members", "code")
				.param("sortType", "INVALID"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(
				"유효하지 않은 sortType입니다. 허용 값: CREATED, NAME, PAID_AT (입력값: INVALID)"
			));
	}

	@Test
	@DisplayName("모임원을 성공적으로 추가한다.")
	void saveAppointmentMember() throws Exception {
		// given
		String code = "code";
		Long groupId = 1L;

		MemberSaveRequest request = new MemberSaveRequest("김반숙");
		MemberResponse response = new MemberResponse(1L, PARTICIPANT, "김반숙",
			"https://moddo-s3.s3.amazonaws.com/profile/1.png", 1L, false, null);

		when(querySettlementService.findIdByCode(code)).thenReturn(groupId);
		when(commandMemberService.addMember(groupId, request)).thenReturn(response);

		// when & then
		mockMvc.perform(post("/api/v1/groups/{code}/members", code)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(response.id()))
			.andExpect(jsonPath("$.role").value(response.role().name()))
			.andExpect(jsonPath("$.name").value("김반숙"))
			.andExpect(jsonPath("$.profile").value(response.profile()))
			.andExpect(jsonPath("$.userId").value(response.userId()))
			.andExpect(jsonPath("$.isPaid").value(response.isPaid()))
			.andExpect(jsonPath("$.paidAt").doesNotExist())
			.andDo(restDocs.document(
				pathParameters(
					parameterWithName("code").description("정산 코드")
				),
				requestFields(
					fieldWithPath("name").type(JsonFieldType.STRING).description("모임원 이름")
				),
				responseFields(
					fieldWithPath("id").type(JsonFieldType.NUMBER).description("모임원 ID"),
					fieldWithPath("role").type(JsonFieldType.STRING).description("모임원 역할"),
					fieldWithPath("name").type(JsonFieldType.STRING).description("모임원 이름"),
					fieldWithPath("profile").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
					fieldWithPath("userId").type(JsonFieldType.NUMBER).description("연결된 사용자 ID"),
					fieldWithPath("isPaid").type(JsonFieldType.BOOLEAN).description("정산 완료 여부"),
					fieldWithPath("paidAt").type(JsonFieldType.NULL).description("정산 완료 시각").optional()
				)
			));
	}

	@Test
	@DisplayName("결제 상태를 성공적으로 변경한다.")
	void updatePaymentStatus() throws Exception {
		// given
		String code = "code";
		Long groupMemberId = 1L;

		PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest(true);
		MemberResponse response = new MemberResponse(1L, PARTICIPANT, "김반숙",
			"https://moddo-s3.s3.amazonaws.com/profile/1.png", 1L, true, LocalDateTime.now());

		when(commandMemberService.updatePaymentStatus(groupMemberId, request)).thenReturn(response);

		// when & then
		mockMvc.perform(
				put("/api/v1/groups/{code}/members/{memberId}", code, groupMemberId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isPaid").value(true))
			.andDo(restDocs.document(
				pathParameters(
					parameterWithName("code").description("정산 코드"),
					parameterWithName("memberId").description("모임원 ID")
				),
				requestFields(
					fieldWithPath("isPaid").type(JsonFieldType.BOOLEAN).description("변경할 결제 상태")
				),
				responseFields(
					fieldWithPath("id").type(JsonFieldType.NUMBER).description("모임원 ID"),
					fieldWithPath("role").type(JsonFieldType.STRING).description("모임원 역할"),
					fieldWithPath("name").type(JsonFieldType.STRING).description("모임원 이름"),
					fieldWithPath("profile").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
					fieldWithPath("userId").type(JsonFieldType.NUMBER).description("연결된 사용자 ID"),
					fieldWithPath("isPaid").type(JsonFieldType.BOOLEAN).description("정산 완료 여부"),
					fieldWithPath("paidAt").type(JsonFieldType.STRING).description("정산 완료 시각").optional()
				)
			));
	}

	@Test
	@DisplayName("모임원을 성공적으로 삭제한다.")
	void deleteAppointmentMember() throws Exception {
		// given
		Long groupMemberId = 1L;

		mockMvc.perform(
				delete("/api/v1/groups/{code}/members/{memberId}", "code", groupMemberId))
			.andExpect(status().isNoContent())
			.andDo(restDocs.document(
				pathParameters(
					parameterWithName("code").description("정산 코드"),
					parameterWithName("memberId").description("삭제할 모임원 ID")
				)
			));

		// when & then
		verify(commandMemberService).delete(groupMemberId);
	}

	@Test
	@DisplayName("로그인 사용자가 참여자를 성공적으로 선택한다.")
	void assignMember() throws Exception {
		String code = "code";
		Long groupId = 1L;
		Long memberId = 2L;
		Long userId = 3L;

		MemberSelectionRequest request = new MemberSelectionRequest(memberId);
		MemberResponse response = new MemberResponse(
			memberId, PARTICIPANT, "김반숙",
			"https://moddo-s3.s3.amazonaws.com/profile/1.png", userId, false, null
		);

		when(loginUserArgumentResolver.supportsParameter(any())).thenReturn(true);
		when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.thenReturn(new LoginUserInfo(userId, "USER"));
		when(querySettlementService.findIdByCode(code)).thenReturn(groupId);
		when(commandMemberService.assignMember(groupId, memberId, userId)).thenReturn(response);

		mockMvc.perform(post("/api/v1/groups/{code}/members/assign", code)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(memberId))
			.andExpect(jsonPath("$.userId").value(userId))
			.andDo(restDocs.document(
				pathParameters(
					parameterWithName("code").description("정산 코드")
				),
				requestFields(
					fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("선택할 모임원 ID")
				),
				responseFields(
					fieldWithPath("id").type(JsonFieldType.NUMBER).description("모임원 ID"),
					fieldWithPath("role").type(JsonFieldType.STRING).description("모임원 역할"),
					fieldWithPath("name").type(JsonFieldType.STRING).description("모임원 이름"),
					fieldWithPath("profile").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
					fieldWithPath("userId").type(JsonFieldType.NUMBER).description("연결된 사용자 ID"),
					fieldWithPath("isPaid").type(JsonFieldType.BOOLEAN).description("정산 완료 여부"),
					fieldWithPath("paidAt").type(JsonFieldType.NULL).description("정산 완료 시각").optional()
				)
			));
	}

	@Test
	@DisplayName("로그인 사용자가 참여자 선택을 성공적으로 해제한다.")
	void unassignMember() throws Exception {
		String code = "code";
		Long groupId = 1L;
		Long memberId = 2L;
		Long userId = 3L;

		MemberSelectionRequest request = new MemberSelectionRequest(memberId);
		MemberResponse response = new MemberResponse(
			memberId, PARTICIPANT, "김반숙",
			"https://moddo-s3.s3.amazonaws.com/profile/1.png", null, false, null
		);

		when(loginUserArgumentResolver.supportsParameter(any())).thenReturn(true);
		when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.thenReturn(new LoginUserInfo(userId, "USER"));
		when(querySettlementService.findIdByCode(code)).thenReturn(groupId);
		when(commandMemberService.unassignMember(groupId, memberId, userId)).thenReturn(response);

		mockMvc.perform(post("/api/v1/groups/{code}/members/unassign", code)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(memberId))
			.andExpect(jsonPath("$.userId").doesNotExist())
			.andDo(restDocs.document(
				pathParameters(
					parameterWithName("code").description("정산 코드")
				),
				requestFields(
					fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("선택 해제할 모임원 ID")
				),
				responseFields(
					fieldWithPath("id").type(JsonFieldType.NUMBER).description("모임원 ID"),
					fieldWithPath("role").type(JsonFieldType.STRING).description("모임원 역할"),
					fieldWithPath("name").type(JsonFieldType.STRING).description("모임원 이름"),
					fieldWithPath("profile").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
					fieldWithPath("userId").type(JsonFieldType.NULL).description("연결된 사용자 ID").optional(),
					fieldWithPath("isPaid").type(JsonFieldType.BOOLEAN).description("정산 완료 여부"),
					fieldWithPath("paidAt").type(JsonFieldType.NULL).description("정산 완료 시각").optional()
				)
			));
	}

}
