package com.example.parknride;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ParknrideApplication {
	public static void main(String[] args) {
		SpringApplication.run(ParknrideApplication.class, args);
	}
}