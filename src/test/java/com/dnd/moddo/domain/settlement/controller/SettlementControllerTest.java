package com.dnd.moddo.domain.settlement.controller;

import static com.dnd.moddo.event.domain.member.ExpenseRole.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.common.logging.ErrorNotifier;
import com.dnd.moddo.event.presentation.request.SearchSettlementListRequest;
import com.dnd.moddo.event.presentation.request.SettlementAccountRequest;
import com.dnd.moddo.event.presentation.request.SettlementRequest;
import com.dnd.moddo.event.presentation.response.MemberResponse;
import com.dnd.moddo.event.presentation.response.SettlementDetailResponse;
import com.dnd.moddo.event.presentation.response.SettlementHeaderResponse;
import com.dnd.moddo.event.presentation.response.SettlementListResponse;
import com.dnd.moddo.event.presentation.response.SettlementResponse;
import com.dnd.moddo.event.presentation.response.SettlementSaveResponse;
import com.dnd.moddo.event.presentation.response.SettlementShareResponse;
import com.dnd.moddo.global.util.RestDocsTestSupport;

public class SettlementControllerTest extends RestDocsTestSupport {

	@MockBean
	ErrorNotifier errorNotifier;

	@Test
	@DisplayName("모임을 성공적으로 생성한다.")
	void saveSettlement() throws Exception {
		// given
		SettlementRequest request = new SettlementRequest("모또 모임");
		SettlementSaveResponse response = new SettlementSaveResponse("groupToken", new MemberResponse(
			1L, MANAGER, "김모또", "https://moddo-s3.s3.amazonaws.com/profile/MODDO.png", true, LocalDateTime.now()
		));

		given(loginUserArgumentResolver.supportsParameter(any()))
			.willReturn(true);

		given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.willReturn(new LoginUserInfo(1L, "USER"));
		given(commandSettlementService.createSettlement(any(), eq(1L))).willReturn(response);

		// when & then
		mockMvc.perform(post("/api/v1/group")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.groupToken").value("groupToken"));
	}

	@Test
	@DisplayName("계좌 정보를 성공적으로 수정한다.")
	void updateAccount() throws Exception {
		// given
		SettlementAccountRequest accountRequest = new SettlementAccountRequest("우리은행", "1111-1111");
		SettlementResponse response = new SettlementResponse(
			1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusMonths(1), "우리은행", "1111-1111",
			LocalDateTime.now().plusDays(1)
		);

		given(loginUserArgumentResolver.supportsParameter(any()))
			.willReturn(true);

		given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.willReturn(new LoginUserInfo(1L, "USER"));
		given(querySettlementService.findIdByCode(anyString())).willReturn(100L);
		given(commandSettlementService.updateAccount(any(), eq(1L), eq(100L))).willReturn(response);

		// when & then
		mockMvc.perform(put("/api/v1/group/account")
				.param("groupToken", "groupToken")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(accountRequest)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("모임을 성공적으로 조회한다.")
	void getSettlement() throws Exception {
		// given
		SettlementDetailResponse response = new SettlementDetailResponse(1L, "모또 모임", List.of(
			new MemberResponse(1L, MANAGER, "김모또", "https://moddo-s3.s3.amazonaws.com/profile/MODDO.png",
				true,
				LocalDateTime.now())
		));

		given(loginUserArgumentResolver.supportsParameter(any()))
			.willReturn(true);

		given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.willReturn(new LoginUserInfo(1L, "USER"));

		given(querySettlementService.findIdByCode(anyString())).willReturn(100L);
		given(querySettlementService.findOne(100L, 1L)).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/v1/group")
				.param("groupToken", "groupToken"))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("모임의 헤더를 성공적으로 조회한다.")
	void getHeader() throws Exception {
		// given
		SettlementHeaderResponse response = SettlementHeaderResponse.of("모또 모임", 10000L,
			LocalDateTime.now().plusDays(1), "우리은행",
			"1111-1111");

		given(querySettlementService.findIdByCode("groupToken")).willReturn(100L);
		given(querySettlementService.findBySettlementHeader(100L)).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/v1/group/header")
				.param("groupToken", "groupToken"))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("속한 정산 리스트를 성공적으로 조회할 수 있다.")
	void searchSettlementList() throws Exception {
		// given
		LocalDateTime fixedTime = LocalDateTime.of(
			2026, 2, 22, 18, 14, 13, 285872000
		);
		List<SettlementListResponse> list = List.of(
			new SettlementListResponse(
				1L,
				"groupCode",
				"모또 모임",
				10000L,
				5L,
				3L,
				fixedTime,
				null
			)
		);

		given(loginUserArgumentResolver.supportsParameter(any()))
			.willReturn(true);

		given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.willReturn(new LoginUserInfo(1L, "USER"));

		given(querySettlementService.search(
			eq(1L),
			any(SearchSettlementListRequest.class)
		)).willReturn(list);

		// when & then
		mockMvc.perform(get("/api/v1/group/list")
				.param("status", "IN_PROGRESS")
				.param("sort", "LATEST")
				.param("limit", "20")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].groupId").value(1L))
			.andExpect(jsonPath("$[0].groupCode").value("groupCode"))
			.andExpect(jsonPath("$[0].name").value("모또 모임"))
			.andExpect(jsonPath("$[0].totalAmount").value(10000L))
			.andExpect(jsonPath("$[0].totalMemberCount").value(5L))
			.andExpect(jsonPath("$[0].completedMemberCount").value(3L))
			.andExpect(jsonPath("$[0].createdAt").value(fixedTime.toString()))
			.andExpect(jsonPath("$[0].completedAt").value(Matchers.nullValue()))
			.andDo(restDocs.document(
				queryParameters(
					parameterWithName("status")
						.description("정산 상태 (ALL | IN_PROGRESS | COMPLETED)")
						.optional(),
					parameterWithName("sort")
						.description("정렬 방식 (LATEST | OLDEST )")
						.optional(),
					parameterWithName("limit")
						.description("조회 개수 제한(min=1, max=100)")
						.optional()
				),
				responseFields(
					fieldWithPath("[].groupId").description("정산 ID"),
					fieldWithPath("[].groupCode").description("정산 코드"),
					fieldWithPath("[].name").description("정산 이름"),
					fieldWithPath("[].totalAmount").description("총 정산 금"),
					fieldWithPath("[].totalMemberCount").description("총 참여자 수"),
					fieldWithPath("[].completedMemberCount").description("입금 완료 참여자 수"),
					fieldWithPath("[].createdAt").description("정산 생성일시"),
					fieldWithPath("[].completedAt").description("정산 완료일시 (완료되지 않은 경우 null)")
				)
			));
	}

	@Test
	@DisplayName("공유용 정산 리스트를 정상적으로 조회할 수 있다.")
	void getShareLinkListSuccess() throws Exception {
		// given
		Long userId = 1L;

		List<SettlementShareResponse> mockList = List.of(
			new SettlementShareResponse(
				1L,
				"모또 모임",
				"groupCode",
				LocalDateTime.of(2026, 1, 1, 12, 0),
				null
			),
			new SettlementShareResponse(
				2L,
				"두번째 모임",
				"groupCode2",
				LocalDateTime.of(2026, 1, 2, 12, 0),
				LocalDateTime.of(2026, 1, 3, 12, 0)
			)
		);

		given(loginUserArgumentResolver.supportsParameter(any()))
			.willReturn(true);

		given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.willReturn(new LoginUserInfo(userId, "USER"));

		given(querySettlementService.findSettlementShareList(userId))
			.willReturn(mockList);

		// when & then
		mockMvc.perform(get("/api/v1/group/share"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].settlementId").value(1L))
			.andExpect(jsonPath("$[0].name").value("모또 모임"))
			.andExpect(jsonPath("$[0].groupCode").value("groupCode"))
			.andExpect(jsonPath("$[0].completedAt").value(Matchers.nullValue()))
			.andDo(restDocs.document(
				responseFields(
					fieldWithPath("[].settlementId").description("정산 ID"),
					fieldWithPath("[].name").description("정산 이름"),
					fieldWithPath("[].groupCode").description("공유 코드"),
					fieldWithPath("[].createdAt").description("생성 일시"),
					fieldWithPath("[].completedAt").description("완료 일시 (완료되지 않았으면 null)").optional()
				)
			));
	}

}
