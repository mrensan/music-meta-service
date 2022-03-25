package net.ensan.musify.thirdparty.musicbrainz;

import net.ensan.musify.musicbrainz.api.ArtistApi;
import net.ensan.musify.musicbrainz.api.ReleaseGroupApi;
import net.ensan.musify.musicbrainz.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MusicbrainzServiceApiClientConfig {
    @Bean("musicbrainzApiClient")
    @Primary
    public ApiClient apiClient(
        @Value("${musicbrainz.url}") final String musicbrainzurl,
        final RestTemplate restTemplate
    ) {
        final ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(musicbrainzurl);
        return apiClient;
    }

    @Bean
    public ArtistApi artistApi(final ApiClient musicbrainzApiClient) {
        return new ArtistApi(musicbrainzApiClient);
    }

    @Bean
    public ReleaseGroupApi releaseGroupApi(final ApiClient musicbrainzApiClient) {
        return new ReleaseGroupApi(musicbrainzApiClient);
    }
}
