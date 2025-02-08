package com.dnd.moddo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	@GetMapping("/test")
	public String testEndpoint() {
		return "Test successful!";
	}
}