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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public Threadblog(String id, String title,String text)
    {
        this.id=id;
        this.title=title;
        this.text = text;
    }

    public String getText() {
        if(text.length()>20) {
            return text.substring(0, 20) + "....";
        }
        else return text;
    }

    public String getAllText()
    {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String text;
}
