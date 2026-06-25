package com.travelplanner.backend.config.external;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.Duration;

@Configuration
public class ExternalApiConfig {

    @Autowired
    private ExternalApiProperties externalApiProperties;

    @Bean(name = "flightRestTemplate")
    public RestTemplate flightRestTemplate() {
        return createRestTemplate(
                externalApiProperties.getFlight().getBaseUrl(),
                externalApiProperties.getFlight().getTimeoutMs()
        );
    }

    @Bean(name = "weatherRestTemplate")
    public RestTemplate weatherRestTemplate() {
        return createRestTemplate(
                externalApiProperties.getWeather().getBaseUrl(),
                externalApiProperties.getWeather().getTimeoutMs()
        );
    }

    @Bean(name = "mapsRestTemplate")
    public RestTemplate mapsRestTemplate() {
        return createRestTemplate(
                externalApiProperties.getMaps().getBaseUrl(),
                externalApiProperties.getMaps().getTimeoutMs()
        );
    }

    private RestTemplate createRestTemplate(String baseUrl, int timeoutMs) {
        RestTemplate restTemplate = new RestTemplate();

        // Set base URL
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(baseUrl);
        restTemplate.setUriTemplateHandler(uriBuilderFactory);

        // Configure timeout
        // Note: You'll need to configure HttpClient for timeouts
        // This is a simplified version

        return restTemplate;
    }
}