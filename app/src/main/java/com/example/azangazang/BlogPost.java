package com.example.azangazang;


import java.util.Date;

public class BlogPost extends BlogPostId {

    public String user_id, image_url, title, thumb;
    public Date timestamp;

    public BlogPost(Date timestamp) {
        this.timestamp = timestamp;
    }

    public BlogPost() {
    }

    public BlogPost(String user_id, String image_uri, String title, String thumb, Date timestamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.title = title;
        this.thumb = thumb;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_uri() {
        return image_url;
    }

    public void setImage_uri(String image_url) {
        this.image_url = image_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
