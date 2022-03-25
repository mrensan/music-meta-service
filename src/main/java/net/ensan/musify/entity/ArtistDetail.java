package net.ensan.musify.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "artist_detail")
public class ArtistDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MBID", nullable = false)
    private String mbid;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "DISAMBIGUATION")
    private String disambiguation;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "FETCHED", columnDefinition = "TINYINT(1)", nullable = false)
    private boolean fetched;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

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

    public boolean isFetched() {
        return fetched;
    }

    public void setFetched(final boolean fetched) {
        this.fetched = fetched;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ArtistDetail that = (ArtistDetail) o;
        return id.equals(that.id) && mbid.equals(that.mbid) && name.equals(that.name) && Objects.equals(
            gender,
            that.gender
        ) && Objects.equals(country, that.country) && Objects.equals(
            disambiguation,
            that.disambiguation
        ) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mbid, name, gender, country, disambiguation, description);
    }
}
