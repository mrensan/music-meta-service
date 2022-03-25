package net.ensan.musify.repository;

import net.ensan.musify.entity.Album;
import net.ensan.musify.entity.ArtistDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    Optional<Album> findByAlbumMbid(String albumMbid);

    List<Album> findAllByArtistDetail(ArtistDetail artistDetail);
}
