package ru.job4j.grabber.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Post {
    private int id;
    private String title;
    private String link;
    private String description;
    private LocalDateTime posted_at;

    public Post(String title, String link, String description, LocalDateTime posted_at) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.posted_at = posted_at;
    }

    public Post(int id, String title, String link, String description, LocalDateTime posted_at) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.description = description;
        this.posted_at = posted_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getPostedAt() {
        return posted_at;
    }

    public void setPostedAt(LocalDateTime posted_at) {
        this.posted_at = posted_at;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return id == post.id && Objects.equals(link, post.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, link);
    }

    @Override
    public String toString() {
        return "Post{"
                + "id="
                + id
                + ", title='"
                + title
                + '\''
                + ", link='"
                + link
                + '\''
                + ", description='"
                + description
                + '\''
                + ", posted_at="
                + posted_at
                + '}';
    }
}
