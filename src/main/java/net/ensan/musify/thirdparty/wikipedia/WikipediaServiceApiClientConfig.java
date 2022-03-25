package net.ensan.musify.thirdparty.wikipedia;

import net.ensan.musify.wikipedia.api.ArtistDetailApi;
import net.ensan.musify.wikipedia.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WikipediaServiceApiClientConfig {
    @Bean("wikipediaApiClient")
    @Primary
    public ApiClient apiClient(
        @Value("${wikipedia.url}") final String wikipediaurl,
        final RestTemplate restTemplate
    ) {
        final ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(wikipediaurl);
        return apiClient;
    }

    @Bean
    public ArtistDetailApi artistDetailApi(final ApiClient wikipediaApiClient) {
        return new ArtistDetailApi(wikipediaApiClient);
    }
}
