package com.eaetirk.expressionevaluator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ExpressionEvaluatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpressionEvaluatorApplication.class, args);
	}

}
