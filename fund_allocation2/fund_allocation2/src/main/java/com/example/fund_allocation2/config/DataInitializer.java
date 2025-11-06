package com.example.fund_allocation2.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dummy")
public class DataInitializer {

    @Bean
    CommandLineRunner seedDefaultUsers() {
        return args -> {
            // No-op: dummy seeding disabled. Enable this profile to seed mock users if needed.
        };
    }
}
