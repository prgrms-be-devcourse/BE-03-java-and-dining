package com.prgms.allen.dining;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DiningApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiningApplication.class, args);
	}

}
