package net.ensan.musify.service;

import net.ensan.musify.entity.Album;
import net.ensan.musify.entity.ArtistDetail;
import net.ensan.musify.musicbrainz.model.QueryResponse;
import net.ensan.musify.musicbrainz.model.ReleaseGroup;
import net.ensan.musify.repository.AlbumRepository;
import net.ensan.musify.repository.ArtistDetailRepository;
import net.ensan.musify.thirdparty.coverartarchive.CoverartarchiveService;
import net.ensan.musify.thirdparty.musicbrainz.MusicbrainzService;
import net.ensan.musify.thirdparty.wikipedia.WikipediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Crawls through all artists and albums and insert all
 * data into database, so that be possible to use quickly
 */
@Service
public class CrawlerService {

    private final MusicbrainzService musicbrainzService;
    private final WikipediaService wikipediaService;
    private final CoverartarchiveService coverartarchiveService;

    private final ArtistDetailRepository artistDetailRepository;
    private final AlbumRepository albumRepository;
    private final Integer maxArtistsLimit;
    private final Integer maxAlbumsLimit;

    @Autowired
    public CrawlerService(
        final MusicbrainzService musicbrainzService,
        final WikipediaService wikipediaService,
        final CoverartarchiveService coverartarchiveService,
        final ArtistDetailRepository artistDetailRepository,
        final AlbumRepository albumRepository,
        @Value("${crowler.max-artists-limit}") final Integer maxArtistsLimit,
        @Value("${crowler.max-albums-limit}") final Integer maxAlbumsLimit
    ) {
        this.musicbrainzService = musicbrainzService;
        this.wikipediaService = wikipediaService;
        this.coverartarchiveService = coverartarchiveService;
        this.artistDetailRepository = artistDetailRepository;
        this.albumRepository = albumRepository;
        this.maxArtistsLimit = maxArtistsLimit;
        this.maxAlbumsLimit = maxAlbumsLimit;
    }

    public void crawl() {
        List<ArtistDetail> artistDetails = artistDetailRepository.findAllByFetchedFalse();
        if (artistDetails.size() < maxArtistsLimit) {
            crawlArtists(artistDetails);
            artistDetails = artistDetailRepository.findAllByFetchedFalse();
            crawlArtistsDescription(artistDetails);
        }
        crawlArtistsAlbums(artistDetails);
    }

    private void crawlArtists(final List<ArtistDetail> artistDetails) {
        QueryResponse queryResponse;
        int offset = artistDetails.size();
        do {
            queryResponse = musicbrainzService.fetchArtists(offset, 100);
            List<ArtistDetail> newArtistDetails = Objects.requireNonNull(queryResponse.getArtists())
                .stream()
                .map(artist -> {
                    final ArtistDetail artistDetail = new ArtistDetail();
                    artistDetail.setMbid(artist.getId());
                    artistDetail.setName(artist.getName());
                    artistDetail.setGender(artist.getGender());
                    artistDetail.setCountry(artist.getCountry());
                    artistDetail.setDisambiguation(artist.getDisambiguation());
                    return artistDetail;
                }).collect(Collectors.toList());
            newArtistDetails.forEach(artistDetail -> {
                if (artistDetailRepository.findByMbid(artistDetail.getMbid()).isEmpty()) {
                    artistDetailRepository.save(artistDetail);
                }
            });
            offset += 100;
        } while (Math.abs(Math.min(Objects.requireNonNull(queryResponse.getCount()), maxArtistsLimit)
            - Objects.requireNonNull(queryResponse.getOffset())) > 100);
    }

    private void crawlArtistsDescription(final List<ArtistDetail> artistDetails) {
        artistDetails.forEach(artistDetail -> {
            if (Objects.isNull(artistDetail.getDescription())) {
                wikipediaService.fetchArtistDescription(artistDetail.getName()).ifPresent(desc -> {
                    artistDetail.setDescription(desc);
                    artistDetailRepository.save(artistDetail);
                });
            }
        });
    }

    private void crawlArtistsAlbums(final List<ArtistDetail> artistDetails) {
        artistDetails.forEach(this::crawlArtistAlbums);
    }

    private void crawlArtistAlbums(final ArtistDetail artistDetail) {
        ReleaseGroup releaseGroup;
        int offset = 0;
        do {
            releaseGroup = musicbrainzService.fetchAlbums(artistDetail.getMbid(), offset, 100);
            releaseGroup.getReleaseGroups().forEach(album -> {
                final Album albumEntity = new Album();
                albumEntity.setArtistDetail(artistDetail);
                albumEntity.setTitle(album.getTitle());
                albumEntity.setAlbumMbid(album.getId());
                coverartarchiveService.fetchCoverart(albumEntity.getAlbumMbid()).ifPresent(albumEntity::setImageUrl);
                if (albumRepository.findByAlbumMbid(albumEntity.getAlbumMbid()).isEmpty()) {
                    albumRepository.save(albumEntity);
                }
            });
            offset += 100;
        } while (
            Math.abs(Math.min(Objects.requireNonNull(releaseGroup.getReleaseGroupCount()), maxAlbumsLimit)
                - Objects.requireNonNull(releaseGroup.getReleaseGroupOffset())) > 100
        );

        artistDetail.setFetched(true);
        artistDetailRepository.save(artistDetail);
    }
}
