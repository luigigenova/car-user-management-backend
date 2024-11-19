package com.desafio;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootTest
@EnableJpaRepositories(basePackages = "com.desafio.*")
class CarUserManagementBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
