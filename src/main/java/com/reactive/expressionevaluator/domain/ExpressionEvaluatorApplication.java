package com.reactive.expressionevaluator.domain;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@EnableRetry
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Logical Expression Evaluator API", version = "1.0", description = "Leapwise - Documentation APIs v1.0"))
public class ExpressionEvaluatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpressionEvaluatorApplication.class, args);
	}

}
