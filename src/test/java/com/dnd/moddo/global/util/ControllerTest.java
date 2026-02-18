package com.dnd.moddo.global.util;

import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.dnd.moddo.auth.application.AuthService;
import com.dnd.moddo.auth.application.KakaoClient;
import com.dnd.moddo.auth.application.RefreshTokenService;
import com.dnd.moddo.auth.infrastructure.security.JwtAuth;
import com.dnd.moddo.auth.infrastructure.security.JwtFilter;
import com.dnd.moddo.auth.infrastructure.security.LoginUserArgumentResolver;
import com.dnd.moddo.auth.presentation.AuthController;
import com.dnd.moddo.common.config.CookieProperties;
import com.dnd.moddo.event.application.command.CommandExpenseService;
import com.dnd.moddo.event.application.command.CommandMemberService;
import com.dnd.moddo.event.application.command.CommandSettlementService;
import com.dnd.moddo.event.application.query.QueryExpenseService;
import com.dnd.moddo.event.application.query.QueryMemberExpenseService;
import com.dnd.moddo.event.application.query.QueryMemberService;
import com.dnd.moddo.event.application.query.QuerySettlementService;
import com.dnd.moddo.event.presentation.ExpenseController;
import com.dnd.moddo.event.presentation.MemberController;
import com.dnd.moddo.event.presentation.MemberExpenseController;
import com.dnd.moddo.event.presentation.SettlementController;
import com.dnd.moddo.image.application.CommandImageService;
import com.dnd.moddo.image.presentation.ImageController;
import com.dnd.moddo.reward.application.QueryCharacterService;
import com.dnd.moddo.reward.presentation.CharacterController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Disabled
@WebMvcTest({
	AuthController.class,
	CharacterController.class,
	ExpenseController.class,
	SettlementController.class,
	MemberController.class,
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
	protected QuerySettlementService querySettlementService;

	@MockBean
	protected CommandSettlementService commandSettlementService;

	@MockBean
	protected QueryMemberService queryMemberService;

	@MockBean
	protected CommandMemberService commandMemberService;

	@MockBean
	protected CommandImageService commandImageService;

	@MockBean
	protected QueryMemberExpenseService queryMemberExpenseService;

	@MockBean
	protected CookieProperties cookieProperties;
	// Jwt
	@MockBean
	protected LoginUserArgumentResolver loginUserArgumentResolver;

	@MockBean
	protected JwtAuth jwtAuth;

	@MockBean
	protected JwtFilter jwtFilter;

	protected String toJson(Object object) throws JsonProcessingException {
		return objectMapper.writeValueAsString(object);
	}

}
