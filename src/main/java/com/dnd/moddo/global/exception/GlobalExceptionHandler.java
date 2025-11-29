package com.dnd.moddo.global.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.dnd.moddo.global.logging.DiscordMessage;
import com.dnd.moddo.global.logging.DiscordNotifier;
import com.dnd.moddo.global.logging.LoggingUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
	private final DiscordNotifier discordNotifier;

	@ExceptionHandler({ModdoException.class})
	public ResponseEntity<ErrorResponse> handleDefineException(ModdoException exception) {
		LoggingUtils.warn(exception);
		return ResponseEntity.status(exception.getStatus())
			.body(new ErrorResponse(exception.getStatus().value(), exception.getMessage()));
	}

	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ResponseEntity<ErrorResponse> handleDefineException(MethodArgumentNotValidException exception) {
		LoggingUtils.warn(exception);

		String message;

		if (exception.getFieldError() == null) {
			message = "";
		} else {
			message = exception.getFieldError().getDefaultMessage();
		}

		return ResponseEntity.status(400)
			.body(new ErrorResponse(400, message));
	}

	@ExceptionHandler({RuntimeException.class})
	public ResponseEntity<ErrorResponse> handleDefineException(RuntimeException exception, WebRequest request) {
		LoggingUtils.error(exception);

		sendDiscordAlarm(exception, request);

		return ResponseEntity.status(500)
			.body(new ErrorResponse(500, "서버에서 알 수 없는 에러가 발생했습니다."));
	}

	private void sendDiscordAlarm(Exception e, WebRequest request) {
		discordNotifier.sendError(createMessage(e, request));
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
