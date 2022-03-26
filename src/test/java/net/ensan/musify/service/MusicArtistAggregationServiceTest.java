package net.ensan.musify.service;

import net.ensan.musify.dto.AlbumDto;
import net.ensan.musify.dto.ArtistDetailDto;
import net.ensan.musify.entity.Album;
import net.ensan.musify.entity.ArtistDetail;
import net.ensan.musify.repository.AlbumRepository;
import net.ensan.musify.repository.ArtistDetailRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MusicArtistAggregationServiceTest {

    private static final String MBID = "MBID";
    private static final String ARTIST_NAME = "ARTIST_NAME";
    private static final String GENDER = "GENDER";
    private static final String COUNTRY = "COUNTRY";
    private static final String DISAMBIGUATION = "DISAMBIGUATION";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String ALBUM_1_MBID = "ALBUM1_MBID";
    private static final String ALBUM_1_TITLE = "ALBUM1_TITLE";
    private static final String IMAGE_URL_1 = "IMAGE_URL_1";
    private static final String ALBUM_2_MBID = "ALBUM_2_MBID";
    private static final String ALBUM_2_TITLE = "ALBUM_2_TITLE";
    private static final String IMAGE_URL_2 = "IMAGE_URL_2";
    private static final String EXP_MSG = "EXP_MSG";

    @Mock
    private ArtistDetailRepository artistDetailRepository;
    @Mock
    private AlbumRepository albumRepository;
    @InjectMocks
    private MusicArtistAggregationService service;

    @Test
    void fetchArtistDetailWhenArtistAvailableShouldReturnArtistDetail() {
        // arrange
        final ArtistDetail artistDetail = generateArtistDetail();
        when(artistDetailRepository.findByMbid(MBID)).thenReturn(Optional.of(artistDetail));
        when(albumRepository.findAllByArtistDetail(artistDetail)).thenReturn(generateAlbums(artistDetail));
        // act
        final ArtistDetailDto dto = service.fetchArtistDetail(MBID);
        // assert
        assertThat(dto)
            .isNotNull()
            .extracting(
                ArtistDetailDto::getMbid,
                ArtistDetailDto::getName,
                ArtistDetailDto::getCountry,
                ArtistDetailDto::getGender,
                ArtistDetailDto::getDisambiguation,
                ArtistDetailDto::getDescription
            ).containsExactly(
                MBID,
                ARTIST_NAME,
                COUNTRY,
                GENDER,
                DISAMBIGUATION,
                DESCRIPTION
            );
        assertThat(dto.getAlbums())
            .isNotNull()
            .hasSize(2)
            .extracting(
                AlbumDto::getId,
                AlbumDto::getTitle,
                AlbumDto::getImageUrl
            ).containsExactly(
                tuple(
                    ALBUM_1_MBID,
                    ALBUM_1_TITLE,
                    IMAGE_URL_1
                ),
                tuple(
                    ALBUM_2_MBID,
                    ALBUM_2_TITLE,
                    IMAGE_URL_2
                )
            );
    }

    @Test
    void fetchArtistDetailWhenArtistUnavailableShouldThrow() {
        // arrange
        when(artistDetailRepository.findByMbid(MBID)).thenThrow(new NoSuchElementException(EXP_MSG));
        // act & assert
        assertThatThrownBy(() ->
            service.fetchArtistDetail(MBID)
        )
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage(EXP_MSG);
    }

    private List<Album> generateAlbums(final ArtistDetail artistDetail) {
        final Album album1 = new Album();
        album1.setAlbumMbid(ALBUM_1_MBID);
        album1.setTitle(ALBUM_1_TITLE);
        album1.setImageUrl(IMAGE_URL_1);
        album1.setArtistDetail(artistDetail);
        final Album album2 = new Album();
        album2.setAlbumMbid(ALBUM_2_MBID);
        album2.setTitle(ALBUM_2_TITLE);
        album2.setImageUrl(IMAGE_URL_2);
        album2.setArtistDetail(artistDetail);
        return List.of(album1, album2);
    }

    private ArtistDetail generateArtistDetail() {
        final ArtistDetail artistDetail = new ArtistDetail();
        artistDetail.setMbid(MBID);
        artistDetail.setName(ARTIST_NAME);
        artistDetail.setGender(GENDER);
        artistDetail.setCountry(COUNTRY);
        artistDetail.setDisambiguation(DISAMBIGUATION);
        artistDetail.setDescription(DESCRIPTION);
        return artistDetail;
    }
}
