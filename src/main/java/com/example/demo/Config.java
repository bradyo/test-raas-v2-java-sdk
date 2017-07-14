package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tangocard.raas.Environments;
import com.tangocard.raas.RaasClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public RaasClient raasClient() {
        com.tangocard.raas.Configuration.environment = Environments.SANDBOX;

        String platformName = "QAPlatform2";
        String platformKey = "apYPfT6HNONpDRUj3CLGWYt7gvIHONpDRUYPfT6Hj";
        return new RaasClient(platformName, platformKey);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
