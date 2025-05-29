package com.dnd.moddo.global.util;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.dnd.moddo.global.config.RestDocsConfig;

@Disabled
@Import(RestDocsConfig.class)
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsTestSupport extends ControllerTest {

	@Autowired
	protected RestDocumentationResultHandler restDocs;

	@BeforeEach
	void setUp(final WebApplicationContext context,
		final RestDocumentationContextProvider provider) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(documentationConfiguration(provider)
				.uris()
				.withScheme("https")
				.withHost("moddo.kro.kr")
				.withPort(443)
				.and()
				.operationPreprocessors()
				.withRequestDefaults(
					modifyUris().host("moddo.kro.kr").removePort(),
					prettyPrint()
				)
				.withResponseDefaults(prettyPrint())
			)
			.alwaysDo(print())
			.alwaysDo(restDocs)
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.build();
	}

	protected MockMultipartHttpServletRequestBuilder multipartPatch(String urlTemplate) {
		MockMultipartHttpServletRequestBuilder builder =
			MockMvcRequestBuilders.multipart(urlTemplate);
		builder.with(request -> {
			request.setMethod(HttpMethod.PATCH.name());
			return request;
		});
		return builder;
	}
}