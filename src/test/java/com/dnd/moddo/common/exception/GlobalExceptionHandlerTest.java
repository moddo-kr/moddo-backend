package com.dnd.moddo.common.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartException;

import com.dnd.moddo.common.logging.DiscordMessage;
import com.dnd.moddo.common.logging.ErrorNotifier;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

	@InjectMocks
	private GlobalExceptionHandler globalExceptionHandler;

	@Mock
	private ErrorNotifier errorNotifier;

	@Test
	void givenInvalidMultipartRequest_thenReturnBadRequestWithoutErrorNotification() {
		// given
		MultipartException exception = new MultipartException("Failed to parse multipart servlet request");

		// when
		ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMultipartException(exception);

		// then
		assertThat(response.getStatusCode().value()).isEqualTo(400);
		assertThat(response.getBody())
			.isEqualTo(new ErrorResponse(400, "잘못된 multipart 요청입니다."));
		then(errorNotifier).should(never()).notifyError(any(DiscordMessage.class));
	}
}
