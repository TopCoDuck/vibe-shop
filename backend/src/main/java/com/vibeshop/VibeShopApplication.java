package com.vibeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VibeShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(VibeShopApplication.class, args);
    }
}
