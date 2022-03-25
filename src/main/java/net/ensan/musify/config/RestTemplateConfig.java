package net.ensan.musify.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder restTemplateBuilder) {
        final HttpClient httpClient =
            HttpClients.custom()
                // to fix org.apache.http.NoHttpResponseException: The target server failed to respond
                .setConnectionReuseStrategy(NoConnectionReuseStrategy.INSTANCE)
                .build();
        return restTemplateBuilder
            .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient))
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(5))
            .build();
    }
}
