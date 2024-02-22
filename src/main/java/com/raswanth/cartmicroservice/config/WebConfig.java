package com.raswanth.cartmicroservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
@Configuration
public class WebConfig {
    @Bean
    public WebClient productClient() {
        return WebClient.create("http://localhost:8081/api/products");
    }
}
