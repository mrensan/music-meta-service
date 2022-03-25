package net.ensan.musify.dto;

public class AlbumDto {

    private String id;
    private String title;
    private String imageUrl;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
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
    public String toString() {
        return "AlbumDto{" +
            "id='" + id + '\'' +
            ", title='" + title + '\'' +
            ", imageUrl='" + imageUrl + '\'' +
            '}';
    }
}
