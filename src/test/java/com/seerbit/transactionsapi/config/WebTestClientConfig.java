package com.seerbit.transactionsapi.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.reactive.server.WebTestClient;

@Configuration
public class WebTestClientConfig {

    @Bean
    public WebTestClient webTestClient() {
        String port = "9090";
        return WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

}
