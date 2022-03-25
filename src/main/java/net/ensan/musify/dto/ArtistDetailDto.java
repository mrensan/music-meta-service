package net.ensan.musify.dto;

import java.util.ArrayList;
import java.util.List;

public class ArtistDetailDto {
    private String mbid;
    private String name;
    private String gender;
    private String country;
    private String disambiguation;
    private String description;
    private List<AlbumDto> albums;

    public String getMbid() {
        return mbid;
    }

    public void setMbid(final String mbid) {
        this.mbid = mbid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(final String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getDisambiguation() {
        return disambiguation;
    }

    public void setDisambiguation(final String disambiguation) {
        this.disambiguation = disambiguation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public synchronized List<AlbumDto> getAlbums() {
        if (albums == null) {
            albums = new ArrayList<>();
        }
        return albums;
    }

    public synchronized void setAlbums(final List<AlbumDto> albums) {
        this.albums = albums;
    }

    @Override
    public String toString() {
        return "ArtistDetailDto{" +
            "mbid='" + mbid + '\'' +
            ", name='" + name + '\'' +
            ", gender='" + gender + '\'' +
            ", country='" + country + '\'' +
            ", disambiguation='" + disambiguation + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
