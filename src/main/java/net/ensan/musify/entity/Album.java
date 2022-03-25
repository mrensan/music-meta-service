package net.ensan.musify.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "album")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTIST_DETAIL_ID", nullable = false)
    private ArtistDetail artistDetail;

    @Column(name = "ALBUM_MBID", nullable = false)
    private String albumMbid;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public ArtistDetail getArtistDetail() {
        return artistDetail;
    }

    public void setArtistDetail(final ArtistDetail artistDetail) {
        this.artistDetail = artistDetail;
    }

    public String getAlbumMbid() {
        return albumMbid;
    }

    public void setAlbumMbid(final String albumMbid) {
        this.albumMbid = albumMbid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Album album = (Album) o;
        return id.equals(album.id)
            && artistDetail.equals(album.artistDetail)
            && albumMbid.equals(album.albumMbid)
            && Objects.equals(title, album.title)
            && Objects.equals(imageUrl, album.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, artistDetail, albumMbid, title, imageUrl);
    }
}
