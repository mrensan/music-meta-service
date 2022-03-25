package net.ensan.musify.rest.delegate;

import net.ensan.musify.api.MusicArtistApiDelegate;
import net.ensan.musify.api.model.MusicArtistDetailsAlbumsDto;
import net.ensan.musify.api.model.MusicArtistDetailsDto;
import net.ensan.musify.dto.ArtistDetailDto;
import net.ensan.musify.service.MusicArtistAggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class MusicArtistApiDelegateService implements MusicArtistApiDelegate {

    private final MusicArtistAggregationService aggregationService;

    @Autowired
    public MusicArtistApiDelegateService(final MusicArtistAggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @Override
    public ResponseEntity<MusicArtistDetailsDto> musicArtistDetails(final String mbid) {
        return ResponseEntity.ok(toMusicArtistDetailsDto(aggregationService.fetchArtistDetail(mbid)));
    }

    private MusicArtistDetailsDto toMusicArtistDetailsDto(final ArtistDetailDto artistDetailDto) {
        return new MusicArtistDetailsDto()
            .name(artistDetailDto.getName())
            .mbid(artistDetailDto.getMbid())
            .gender(artistDetailDto.getGender())
            .country(artistDetailDto.getCountry())
            .disambiguation(artistDetailDto.getDisambiguation())
            .description(artistDetailDto.getDescription())
            .albums(
                artistDetailDto.getAlbums().stream()
                    .map(
                        albumsDto -> new MusicArtistDetailsAlbumsDto()
                            .id(albumsDto.getId())
                            .title(albumsDto.getTitle())
                            .imageUrl(albumsDto.getImageUrl())
                    ).collect(Collectors.toList())
            );
    }
}
