package com.construapp.construapp.models;

import java.util.List;

/**
 * Created by jose on 12-10-17.
 */

public class MultimediaFiles {
    private List<MultimediaFile> multimediaFiles;

    public MultimediaFiles(List<MultimediaFile> multimediaFiles) {
        this.multimediaFiles= multimediaFiles;
    }

    public List<MultimediaFile> getMultimediaFiles() {
        return multimediaFiles;
    }

    public void setMultimediaFiles(List<MultimediaFile> multimediaFiles) {
        this.multimediaFiles = multimediaFiles;
    }
}
