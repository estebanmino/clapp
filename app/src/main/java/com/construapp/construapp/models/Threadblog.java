package com.construapp.construapp.models;

/**
 * Created by jose on 06-11-17.
 */

public class Threadblog {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    private String userThreadId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public Threadblog(String id, String title,String text, String userThreadId)
    {
        this.id=id;
        this.title=title;
        this.text = text;
        this.userThreadId = userThreadId;
    }

    public String getText() {
        if(text.length()>40) {
            return text.substring(0, 40) + "....";
        }
        else return text;
    }

    public String getAllText()
    {
        return text;
    }
    public String getUserThreadId(){return userThreadId;}

    public void setText(String text) {
        this.text = text;
    }
    public void setUserThreadId (String userThreadId) { this.userThreadId = userThreadId;}

    private String text;
}
