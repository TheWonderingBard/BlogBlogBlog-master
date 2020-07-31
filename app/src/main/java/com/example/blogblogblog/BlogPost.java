package com.example.blogblogblog;

import com.google.firebase.auth.FirebaseUser;

import java.sql.Timestamp;
import java.util.Date;

public class BlogPost {
    private String userId, image_url, title, content;
    private java.util.Date timestamp;

    public String getDescription() {
        return content;
    }

    public BlogPost() {

    }

    public java.util.Date getTimestamp() {
        return timestamp;
    }




    public BlogPost(String userId, String image_url, String title, String content, Date timestamp) {
        this.userId = userId;
        this.image_url = image_url;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getImage_url() {
        return image_url;
    }
}
