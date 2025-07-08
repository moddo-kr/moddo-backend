package com.dnd.moddo.global.util;

import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.dnd.moddo.domain.auth.controller.AuthController;
import com.dnd.moddo.domain.auth.service.AuthService;
import com.dnd.moddo.domain.auth.service.KakaoClient;
import com.dnd.moddo.domain.auth.service.RefreshTokenService;
import com.dnd.moddo.domain.character.controller.CharacterController;
import com.dnd.moddo.domain.character.service.QueryCharacterService;
import com.dnd.moddo.domain.expense.controller.ExpenseController;
import com.dnd.moddo.domain.expense.service.CommandExpenseService;
import com.dnd.moddo.domain.expense.service.QueryExpenseService;
import com.dnd.moddo.domain.group.controller.GroupController;
import com.dnd.moddo.domain.group.service.CommandGroupService;
import com.dnd.moddo.domain.group.service.QueryGroupService;
import com.dnd.moddo.domain.groupMember.controller.GroupMemberController;
import com.dnd.moddo.domain.groupMember.service.CommandGroupMemberService;
import com.dnd.moddo.domain.groupMember.service.QueryGroupMemberService;
import com.dnd.moddo.domain.image.controller.ImageController;
import com.dnd.moddo.domain.image.service.CommandImageService;
import com.dnd.moddo.domain.memberExpense.controller.MemberExpenseController;
import com.dnd.moddo.domain.memberExpense.service.QueryMemberExpenseService;
import com.dnd.moddo.global.jwt.auth.JwtAuth;
import com.dnd.moddo.global.jwt.auth.JwtFilter;
import com.dnd.moddo.global.jwt.service.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Disabled
@WebMvcTest({
	AuthController.class,
	CharacterController.class,
	ExpenseController.class,
	GroupController.class,
	GroupMemberController.class,
	ImageController.class,
	MemberExpenseController.class
})
public abstract class ControllerTest {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	// Service
	@MockBean
	protected JwtService jwtService;

	@MockBean
	protected AuthService authService;

	@MockBean
	protected KakaoClient kakaoClient;

	@MockBean
	protected RefreshTokenService refreshTokenService;

	@MockBean
	protected QueryCharacterService queryCharacterService;

	@MockBean
	protected QueryExpenseService queryExpenseService;

	@MockBean
	protected CommandExpenseService commandExpenseService;

	@MockBean
	protected QueryGroupService queryGroupService;

	@MockBean
	protected CommandGroupService commandGroupService;

	@MockBean
	protected QueryGroupMemberService queryGroupMemberService;

	@MockBean
	protected CommandGroupMemberService commandGroupMemberService;

	@MockBean
	protected CommandImageService commandImageService;

	@MockBean
	protected QueryMemberExpenseService queryMemberExpenseService;

	// Jwt
	@MockBean
	protected JwtAuth jwtAuth;

	@MockBean
	protected JwtFilter jwtFilter;

	protected String toJson(Object object) throws JsonProcessingException {
		return objectMapper.writeValueAsString(object);
	}

}
