package com.construapp.construapp.models;

import java.util.ArrayList;

/**
 * Created by jose on 06-11-17.
 */

public class ThreadBlog {


    private String id;
    private String userThreadId;
    private String title;
    private String text;

    private ArrayList<MultimediaFile> multimediaPictureFiles;
    private ArrayList<MultimediaFile> multimediaAudioFiles;
    private ArrayList<MultimediaFile> multimediaDocumentsFiles;
    private ArrayList<MultimediaFile> multimediaVideosFiles;

    public ArrayList<MultimediaFile> getMultimediaPictureFiles() {
        return multimediaPictureFiles;
    }

    public void setMultimediaPictureFiles(ArrayList<MultimediaFile> multimediaPictureFiles) {
        this.multimediaPictureFiles = multimediaPictureFiles;
    }

    public ArrayList<MultimediaFile> getMultimediaAudioFiles() {
        return multimediaAudioFiles;
    }

    public void setMultimediaAudioFiles(ArrayList<MultimediaFile> multimediaAudioFiles) {
        this.multimediaAudioFiles = multimediaAudioFiles;
    }

    public ArrayList<MultimediaFile> getMultimediaDocumentsFiles() {
        return multimediaDocumentsFiles;
    }

    public void setMultimediaDocumentsFiles(ArrayList<MultimediaFile> multimediaDocumentsFiles) {
        this.multimediaDocumentsFiles = multimediaDocumentsFiles;
    }

    public ArrayList<MultimediaFile> getMultimediaVideosFiles() {
        return multimediaVideosFiles;
    }

    public void setMultimediaVideosFiles(ArrayList<MultimediaFile> multimediaVideosFiles) {
        this.multimediaVideosFiles = multimediaVideosFiles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public ArrayList<String> getAddedMultimediaKeysS3(){
        ArrayList<String> addedKeys = new ArrayList<>();
        String start = Constants.S3_LESSONS_PATH+"/"+id+"/";
        String key = "";

        for (MultimediaFile mmVideo: multimediaVideosFiles) {
            if (mmVideo.getAdded() == 1) {
                key = Constants.S3_VIDEOS_PATH+"/"+mmVideo.getmPath().substring(mmVideo.getmPath().lastIndexOf("/")+1);
                addedKeys.add(start+key);
            }
        }
        for (MultimediaFile mmVideo: multimediaDocumentsFiles) {
            if (mmVideo.getAdded() == 1) {
                key = Constants.S3_DOCS_PATH+"/"+mmVideo.getmPath().substring(mmVideo.getmPath().lastIndexOf("/")+1);
                addedKeys.add(start+key);
            }
        }
        for (MultimediaFile mmVideo: multimediaAudioFiles) {
            if (mmVideo.getAdded() == 1) {
                key = Constants.S3_AUDIOS_PATH+"/"+mmVideo.getmPath().substring(mmVideo.getmPath().lastIndexOf("/")+1);
                addedKeys.add(start+key);
            }
        }
        for (MultimediaFile mmVideo: multimediaPictureFiles) {
            if (mmVideo.getAdded() == 1) {
                key = Constants.S3_IMAGES_PATH+"/"+mmVideo.getmPath().substring(mmVideo.getmPath().lastIndexOf("/")+1);
                addedKeys.add(start+key);
            }
        }
        return addedKeys;
    }

    public Boolean isEmptyMultimedia() {
        return multimediaAudioFiles.size() == 0 && multimediaPictureFiles.size() == 0
                && multimediaDocumentsFiles.size() == 0 && multimediaVideosFiles.size() == 0;
    }

    public Boolean hasMultimediaFiles() {
        return
                multimediaPictureFiles.size() > 0 ||
                        multimediaAudioFiles.size() > 0 ||
                        multimediaVideosFiles.size() > 0 ||
                        multimediaDocumentsFiles.size() > 0 ;
    }

    public void initMultimediaFiles() {
        this.multimediaPictureFiles = new ArrayList<>();
        this.multimediaAudioFiles = new ArrayList<>();
        this.multimediaDocumentsFiles = new ArrayList<>();
        this.multimediaVideosFiles = new ArrayList<>();
    }


}
