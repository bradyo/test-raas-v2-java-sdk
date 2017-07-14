package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tangocard.raas.Environments;
import com.tangocard.raas.RaasClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class Config {

    @Bean
    public RaasClient raasClient(Environment environment) {
        // Set API target using this global Configuration singleton
        com.tangocard.raas.Configuration.environment = Environments.SANDBOX;

        String platformName = environment.getProperty("platformName");
        String platformKey = environment.getProperty("platformKey");
        return new RaasClient(platformName, platformKey);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
