package com.assignment.recipe_sharing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class RecipeSharingApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecipeSharingApplication.class, args);
	}

}
