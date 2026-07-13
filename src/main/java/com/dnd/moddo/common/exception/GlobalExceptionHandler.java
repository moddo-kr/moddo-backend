package com.dnd.moddo.common.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

import com.dnd.moddo.common.logging.DiscordMessage;
import com.dnd.moddo.common.logging.ErrorNotifier;
import com.dnd.moddo.common.logging.LoggingUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
	private final ErrorNotifier errorNotifier;

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(
		MethodArgumentTypeMismatchException exception
	) {

		LoggingUtils.warn(exception);

		String parameterName = exception.getName();
		Object invalidValue = exception.getValue();
		Class<?> requiredType = exception.getRequiredType();

		String message;

		if (requiredType != null && requiredType.isEnum()) {
			String allowedValues = String.join(", ",
				Arrays.stream(requiredType.getEnumConstants())
					.map(Object::toString)
					.toList()
			);

			message = String.format(
				"'%s' 값 '%s'은(는) 올바르지 않습니다. 허용 값: %s",
				parameterName,
				invalidValue,
				allowedValues
			);
		} else {
			message = String.format(
				"'%s' 값 '%s'은(는) 올바른 형식이 아닙니다.",
				parameterName,
				invalidValue
			);
		}

		return ResponseEntity.badRequest()
			.body(new ErrorResponse(400, message));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(
		MethodArgumentNotValidException e
	) {

		LoggingUtils.warn(e);

		String message = e.getMessage();

		return ResponseEntity.badRequest()
			.body(new ErrorResponse(400, message));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(
		ConstraintViolationException e
	) {

		return ResponseEntity.badRequest()
			.body(new ErrorResponse(400, e.getMessage()));
	}

	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<ErrorResponse> handleMultipartException(MultipartException exception) {
		LoggingUtils.warn(exception);

		return ResponseEntity.badRequest()
			.body(new ErrorResponse(400, "잘못된 multipart 요청입니다."));
	}

	@ExceptionHandler({ModdoException.class})
	public ResponseEntity<ErrorResponse> handleDefineException(ModdoException exception) {
		LoggingUtils.warn(exception);
		return ResponseEntity.status(exception.getStatus())
			.body(new ErrorResponse(exception.getStatus().value(), exception.getMessage()));
	}

	@ExceptionHandler({RuntimeException.class})
	public ResponseEntity<ErrorResponse> handleDefineException(RuntimeException exception, WebRequest request) {
		LoggingUtils.error(exception);

		sendDiscordAlarm(exception, request);

		return ResponseEntity.status(500)
			.body(new ErrorResponse(500, "서버에서 알 수 없는 에러가 발생했습니다."));
	}

	private void sendDiscordAlarm(Exception e, WebRequest request) {
		errorNotifier.notifyError(createMessage(e, request));
	}

	private DiscordMessage createMessage(Exception e, WebRequest request) {
		HttpServletRequest httpReq = ((ServletWebRequest)request).getRequest();

		String method = httpReq.getMethod();
		String url = createRequestFullPath(request);
		String ip = getClientIp(httpReq);
		String ua = httpReq.getHeader("User-Agent");
		String exceptionType = e.getClass().getName();
		String message = (e.getMessage() == null ? "No message" : e.getMessage());

		return DiscordMessage.builder()
			.content("# 🚨 모또 서버 에러 발생!")
			.embeds(List.of(
				DiscordMessage.Embed.builder()
					.title("")
					.description(
						"### 🕖 발생 시간 \n" + LocalDateTime.now() + "\n\n" +
							"### 🔗 요청 URL\n" + url + "\n\n" +
							"### 👤 사용자 정보\n" +
							"- IP : " + ip + "\n" +
							"- User-Agent : " + ua + "\n\n" +
							"### 🐛 예외 타입\n" + exceptionType + "\n\n" +
							"### ❗ 메시지\n" + message + "\n\n" +
							"### 📄 Stack Trace\n" +
							"```\n" + getStackTrace(e).substring(0, 1500) + "\n```"
					)
					.build()
			))
			.build();
	}

	private String getClientIp(HttpServletRequest request) {
		String xf = request.getHeader("X-Forwarded-For");
		if (xf != null) {
			return xf.split(",")[0];
		}
		return request.getRemoteAddr();
	}

	private String createRequestFullPath(WebRequest webRequest) {
		HttpServletRequest request = ((ServletWebRequest)webRequest).getRequest();
		String fullPath = request.getMethod() + " " + request.getRequestURL();

		String queryString = request.getQueryString();
		if (queryString != null) {
			fullPath += "?" + queryString;
		}

		return fullPath;
	}

	private String getStackTrace(Exception e) {
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}
}
