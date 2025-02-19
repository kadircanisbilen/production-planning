package com.production.planning;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Production Planning API", version = "1.0", description = "Documentation for Production Planning API"))
@SpringBootApplication
public class ProductionPlanningApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductionPlanningApplication.class, args);
	}

}
