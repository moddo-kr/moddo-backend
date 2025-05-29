package com.dnd.moddo.global.util;

import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.dnd.moddo.domain.auth.controller.AuthController;
import com.dnd.moddo.domain.auth.service.AuthService;
import com.dnd.moddo.domain.auth.service.RefreshTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Disabled
@WebMvcTest({
	AuthController.class
})
public abstract class ControllerTest {

	@Autowired
	public MockMvc mockMvc;

	@Autowired
	public ObjectMapper objectMapper;

	// Service

	// Auth
	@MockBean
	public AuthService authService;

	@MockBean
	public RefreshTokenService refreshTokenService;

	protected String toJson(Object object) throws JsonProcessingException {
		return objectMapper.writeValueAsString(object);
	}

}
