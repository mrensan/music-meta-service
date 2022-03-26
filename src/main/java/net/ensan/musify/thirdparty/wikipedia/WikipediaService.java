package net.ensan.musify.thirdparty.wikipedia;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import net.ensan.musify.wikipedia.api.ArtistDetailApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A service to fetch the artists' description. Calling this service's
 * methods rate is limited due to the limit of third-party contract.
 */
@Service
@RateLimiter(name = "wikipedia")
@Retry(name = "wikipedia")
public class WikipediaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikipediaService.class);

    private final ArtistDetailApi artistDetailApi;
    private final Configuration configuration;
    private final TypeRef<List<String>> typeRef;

    @Autowired
    public WikipediaService(final ArtistDetailApi artistDetailApi) {
        this.artistDetailApi = artistDetailApi;
        this.configuration = Configuration
            .builder()
            .mappingProvider(new JacksonMappingProvider())
            .jsonProvider(new JacksonJsonProvider())
            .build();
        this.typeRef = new TypeRef<>() {};
    }

    /**
     * Fetches an artist's description by their title.
     *
     * url example:
     * "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=true&redirects=true&titles=Michael Jackson"
     *
     * @param title the desired title to fetch the description based on it.
     * @return an optional of the description string
     */
    public Optional<String> fetchArtistDescription(final String title) {
        try {
            LOGGER.info("fetchArtistDescription is called, title={}", title);
            final ResponseEntity<String> responseEntity = artistDetailApi.apiPhpGetWithHttpInfo(
                title,
                "query",
                Boolean.TRUE,
                "extracts",
                "json",
                Boolean.TRUE
            );
            final DocumentContext jsonContext = JsonPath.using(configuration).parse(responseEntity.getBody());
            List<String> extracts = jsonContext.read("$['query']['pages'][*]['extract']", typeRef);
            return Optional.ofNullable(Objects.isNull(extracts) || extracts.isEmpty() ? null : extracts.get(0));
        } catch (RestClientException e) {
            LOGGER.warn("artist description not found for title: {}", title);
            return Optional.empty();
        }
    }
}
