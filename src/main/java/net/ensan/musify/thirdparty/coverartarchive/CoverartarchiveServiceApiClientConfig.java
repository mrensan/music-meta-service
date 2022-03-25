package net.ensan.musify.thirdparty.coverartarchive;

import net.ensan.musify.coverartarchive.api.ReleaseGroupImageApi;
import net.ensan.musify.coverartarchive.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CoverartarchiveServiceApiClientConfig {
    @Bean("coverartarchiveApiClient")
    @Primary
    public ApiClient apiClient(
        @Value("${coverartarchive.url}") final String coverartarchiveurl,
        final RestTemplate restTemplate
    ) {
        final ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(coverartarchiveurl);
        return apiClient;
    }

    @Bean
    public ReleaseGroupImageApi releaseGroupImageApi(final ApiClient coverartarchiveApiClient) {
        return new ReleaseGroupImageApi(coverartarchiveApiClient);
    }
}
