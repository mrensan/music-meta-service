package net.ensan.musify.service;

import net.ensan.musify.config.CacheConfig;
import net.ensan.musify.dto.AlbumDto;
import net.ensan.musify.dto.ArtistDetailDto;
import net.ensan.musify.entity.Album;
import net.ensan.musify.entity.ArtistDetail;
import net.ensan.musify.repository.AlbumRepository;
import net.ensan.musify.repository.ArtistDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class MusicArtistAggregationService {

    private final ArtistDetailRepository artistDetailRepository;
    private final AlbumRepository albumRepository;

    @Autowired
    public MusicArtistAggregationService(
        final ArtistDetailRepository artistDetailRepository,
        final AlbumRepository albumRepository
    ) {
        this.artistDetailRepository = artistDetailRepository;
        this.albumRepository = albumRepository;
    }

    /**
     * Fetches artist detail from database based on given
     * MBID and return a populated {@code ArtistDetailDto}
     * @param mbid the desired string of MBID to fetch data based on it
     * @return a populated {@code ArtistDetailDto} instance
     */
    @Cacheable(cacheNames = CacheConfig.ARTIST_CACHE_NAME)
    public ArtistDetailDto fetchArtistDetail(final String mbid) {
        final ArtistDetail artistDetail =
            artistDetailRepository.findByMbid(mbid).orElseThrow(() -> new NoSuchElementException("Artist not found"));
        final ArtistDetailDto artistDetailDto = new ArtistDetailDto();
        artistDetailDto.setMbid(artistDetail.getMbid());
        artistDetailDto.setName(artistDetail.getName());
        artistDetailDto.setGender(artistDetail.getGender());
        artistDetailDto.setCountry(artistDetail.getCountry());
        artistDetailDto.setDisambiguation(artistDetail.getDisambiguation());
        artistDetailDto.setDescription(artistDetail.getDescription());
        final List<Album> albums = albumRepository.findAllByArtistDetail(artistDetail);
        artistDetailDto.setAlbums(
            albums.stream().map(album -> {
                    final AlbumDto albumDto = new AlbumDto();
                    albumDto.setId(album.getAlbumMbid());
                    albumDto.setTitle(album.getTitle());
                    albumDto.setImageUrl(album.getImageUrl());
                    return albumDto;
                })
                .collect(Collectors.toList())
        );
        return artistDetailDto;
    }
}
