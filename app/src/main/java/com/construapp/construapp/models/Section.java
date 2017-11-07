package com.construapp.construapp.models;

/**
 * Created by jose on 06-11-17.
 */

public class Section {

    public Section(String id,String name, String description)
    {
        this.id=id;;
        this.name=name;
        this.description=description;

    }
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;
}
