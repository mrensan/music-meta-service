package net.ensan.musify.repository;

import net.ensan.musify.entity.ArtistDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArtistDetailRepository extends JpaRepository<ArtistDetail, Long> {

    Optional<ArtistDetail> findByMbid(String mbid);

    List<ArtistDetail> findAllByFetchedFalse();
}
