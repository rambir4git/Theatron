package com.example.theatrondemo;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class SocialPost {
    private String title;
    private String media;
    private String creatorID;
    private String postID;
    private List<String> likes;
    private List<String> commentators;
    private List<String> shares;
    private List<String> views;
    private Timestamp timestamp;

    SocialPost() {
        //required for firestore
    }

    SocialPost(String title, String mediaURL, String creatorID, String postID, Timestamp timestamp) {
        this.title = title;
        this.media = mediaURL;
        this.creatorID = creatorID;
        this.postID = postID;
        this.timestamp = timestamp;
        likes = new ArrayList<>();
        commentators = new ArrayList<>();
        shares = new ArrayList<>();
        views = new ArrayList<>();
    }

    //getters for firebase
    public String getTitle() {
        return title;
    }

    public String getMedia() {
        return media;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public String getPostID() {
        return postID;
    }

    public List<String> getLikes() {
        return likes;
    }

    public List<String> getShares() {
        return shares;
    }

    public List<String> getViews() {
        return views;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    //setters for firebase
    public void setTitle(String title) {
        this.title = title;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<String> getCommentators() {
        return commentators;
    }

    public void setCommentators(List<String> commentators) {
        this.commentators = commentators;
    }

    public void setShares(List<String> shares) {
        this.shares = shares;
    }

    public void setViews(List<String> views) {
        this.views = views;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public static class Comment {
        String message;
        String userId;
        String commentMedia;
        Timestamp timestamp;

        public Comment() {
            //required for firestore
        }

        public Comment(String message, String userId, String commentMedia, Timestamp timestamp) {
            this.message = message;
            this.userId = userId;
            this.commentMedia = commentMedia;
            this.timestamp = timestamp;
        }

        public String getMessage() {
            return message;
        }

        public String getUserId() {
            return userId;
        }

        public void setMessage(String comment) {
            this.message = comment;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        public String getCommentMedia() {
            return commentMedia;
        }

        public void setCommentMedia(String commentMedia) {
            this.commentMedia = commentMedia;
        }
    }
}
