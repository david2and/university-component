package com.javeriana.component;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ComponentApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComponentApplication.class, args);
	}

}
