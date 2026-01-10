package com.dnd.moddo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/test")
public class ErrorTestController {

	@GetMapping("/force-error")
	public String forceError() {
		throw new RuntimeException("🔥 강제 에러 발생 테스트");
	}

}
