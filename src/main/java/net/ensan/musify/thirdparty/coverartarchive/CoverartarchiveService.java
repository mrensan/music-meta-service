package net.ensan.musify.thirdparty.coverartarchive;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import net.ensan.musify.coverartarchive.api.ReleaseGroupImageApi;
import net.ensan.musify.coverartarchive.model.ReleaseGroupImageImages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.Objects;
import java.util.Optional;

/**
 * A service to fetch cover arts. Calling this service's
 * methods rate is limited due to the limit of third-party
 * contract.
 */
@Service
@RateLimiter(name = "coverartarchive")
@Retry(name = "coverartarchive")
public class CoverartarchiveService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoverartarchiveService.class);

    private final ReleaseGroupImageApi releaseGroupImageApi;

    public CoverartarchiveService(final ReleaseGroupImageApi releaseGroupImageApi) {
        this.releaseGroupImageApi = releaseGroupImageApi;
    }

    /**
     * Fetches the cover art url based on the album MBID.
     *
     * url example:
     * "http://coverartarchive.org/release-group/07b77678-41cc-3457-811c-8b4e5ca18798"
     *
     * @param mbid the MBID of the desired album to fetch the cover art url
     * @return an optional of the string of cover art url
     */
    public Optional<String> fetchCoverart(final String mbid) {
        try {
            LOGGER.info("fetchCoverart is called mbid={}", mbid);
            return Objects.requireNonNull(releaseGroupImageApi.releaseGroupMbidGet(mbid).getImages())
                .stream()
                .filter(ReleaseGroupImageImages::getFront)
                .map(ReleaseGroupImageImages::getImage)
                .findFirst();
        } catch (RestClientException e) {
            LOGGER.warn("exception happened: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
