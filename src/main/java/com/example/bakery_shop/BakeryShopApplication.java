package com.example.bakery_shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class BakeryShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(BakeryShopApplication.class, args);
	}

}
