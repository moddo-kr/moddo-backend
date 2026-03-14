package com.dnd.moddo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ConfigurationPropertiesScan
@EnableFeignClients
@EnableScheduling
public class ModdoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModdoApplication.class, args);
	}

}
