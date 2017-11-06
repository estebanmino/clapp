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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public Threadblog(String id, String name)
    {
        this.id=id;
        this.name=name;
    }
}
