package com.desafio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.desafio")
public class CarUserManagementBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarUserManagementBackendApplication.class, args);
	}

}
