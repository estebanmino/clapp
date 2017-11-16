package com.construapp.construapp.models;

/**
 * Created by jose on 09-11-17.
 */

public class Post {

    public Post() {
        setId("");
        setText("");
        setFirst_name("");
        setLast_name("");
        setPosition("");
        setTimestamp("");
        setPostUserId("");
    }

    private String text;
    private String id;
    private String authorId;
    private String position;
    private String first_name;
    private String last_name;
    private String timestamp;

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    private String postUserId;

    public String getId() {
        return id;
    }

    public String getPostUserId(){return postUserId;}

    public void setPostUserId(String postUserId) {this.postUserId = postUserId;}

    public void setId(String id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
