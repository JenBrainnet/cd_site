package ru.job4j.site.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.job4j.site.handler.RestTemplateResponseErrorHandler;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final RestTemplateResponseErrorHandler errorHandler;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .errorHandler(errorHandler)
                .build();
    }
}
