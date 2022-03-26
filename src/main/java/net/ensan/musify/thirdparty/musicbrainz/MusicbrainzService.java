package net.ensan.musify.thirdparty.musicbrainz;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import net.ensan.musify.musicbrainz.api.QueryResponseApi;
import net.ensan.musify.musicbrainz.api.ReleaseGroupApi;
import net.ensan.musify.musicbrainz.model.QueryResponse;
import net.ensan.musify.musicbrainz.model.ReleaseGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

/**
 * A service to fetch artists and artists' albums. Calling this service's
 * methods rate is limited due to the limit of third-party contract.
 */
@Service
@RateLimiter(name = "musicbrainz")
@Retry(name = "musicbrainz")
public class MusicbrainzService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MusicbrainzService.class);

    private final ReleaseGroupApi releaseGroupApi;
    private final QueryResponseApi queryResponseApi;

    private final String fmt;
    private final String userAgent;
    private final String type;
    private final String query;

    @Autowired
    public MusicbrainzService(
        final ReleaseGroupApi releaseGroupApi,
        final QueryResponseApi queryResponseApi,
        @Value("${musicbrainz.fmt}") final String fmt,
        @Value("${musicbrainz.user-agent}") final String userAgent,
        @Value("${musicbrainz.type}") final String type,
        @Value("${musicbrainz.query}") final String query
    ) {
        this.releaseGroupApi = releaseGroupApi;
        this.queryResponseApi = queryResponseApi;
        this.fmt = fmt;
        this.userAgent = userAgent;
        this.type = type;
        this.query = query;
    }

    /**
     * Fetches a list of artist's release groups of album type, limited
     * with "offset" and "limit" parameters.
     *
     * url example:
     * "http://musicbrainz.org/ws/2/release-group?artist=24f1766e-9635-4d58-a4d4-9413f9f98a4c&fmt=json&type=album&offset=0&limit=100"
     *
     * @param mbid the desired artist's MBID
     * @param offset the desired offset of the release groups
     * @param limit the desired limit of the release groups
     * @return an instance of {@code ReleaseGroup} contains meta-data and a list of release groups
     */
    public ReleaseGroup fetchAlbums(String mbid, Integer offset, Integer limit) {
        try {
            LOGGER.info("fetchAlbums is called, mbid={} offset={} limit={}", mbid, offset, limit);
            return releaseGroupApi.releaseGroupGet(mbid, type, fmt, offset, limit, userAgent);
        } catch (RestClientException e) {
            throw new IllegalArgumentException(String.format(
                "fetchAlbums was unsuccessful, mbid=%s offset=%d, limit=%d",
                mbid,
                offset,
                limit
            ), e);
        }
    }

    /**
     * Queries for a list of artists, limited with "offset" and "limit".
     *
     * url example:
     * "http://musicbrainz.org/ws/2/artist/?query=type:person&offset=100&limit=100&fmt=json"
     *
     * @param offset the desired offset of the artists
     * @param limit the desired limit of the artists
     * @return an instance of {@code QueryResponse} contains meta-data and a list of artists.
     */
    public QueryResponse fetchArtists(final Integer offset, final Integer limit) {
        try {
            LOGGER.info("fetchArtists is called, offset={} limit={}", offset, limit);
            return queryResponseApi.artistGet(offset, limit, query, fmt, userAgent);
        } catch (RestClientException e) {
            throw new IllegalArgumentException(String.format(
                "fetchArtists was unsuccessful, offset=%d, limit=%d",
                offset,
                limit
            ), e);
        }
    }
}
