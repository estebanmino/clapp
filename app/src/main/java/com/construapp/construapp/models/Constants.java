package com.construapp.construapp.models;

/**
 * Created by ESTEBANFML on 18-10-2017.
 */

public interface Constants {

    // General
    public static final String BASE_URL = "http://construapp-api.ing.puc.cl";
    public static final String COMPANIES = "companies";
    public static final String LESSONS = "lessons";
    public static final String USERS = "users";
    public static final String GET_PROJECTS = "get_projects";
    public static final String GET_PERMISSION = "get_permission";
    public static final String PROJECTS = "projects";
    public static final String SESSIONS = "sessions";
    public static final String SAVE_KEY = "save_key";


    // Shared preferences
    public static final String SP_CONSTRUAPP = "ConstruApp";
    public static final String SP_COMPANY = "company_id";
    public static final String SP_USER = "user_id";
    public static final String SP_TOKEN = "token";
    public static final String SP_HAS_PROJECTS = "has_projects";
    public static final String SP_HAS_PERMISSION = "has_permission";
    public static final String SP_PROJECTS = "projects";
    public static final String SP_ACTUAL_PROJECT = "actual_project";
    public static final String SP_USER_PERMISSION_NAME = "name_permission";
    public static final String SP_USER_PERMISSION = "user_permission";

    //Queries
    public static final String Q_AUTHORIZATION = "Authorization";
    public static final String Q_CONTENTTYPE = "Content-Type";
    public static final String Q_CONTENTTYPE_JSON = "application/json";
    public static final String Q_CONTENTTYPE_JSON_UTF8 = "application/json; charset=utf-8";

    //S3
    public static final String S3_BUCKET = "construapp";
    public static final String S3_IMAGES_PATH = "images";
    public static final String S3_VIDEOS_PATH = "videos";
    public static final String S3_AUDIOS_PATH = "audios";
    public static final String S3_DOCS_PATH = "docs";
    public static final String S3_LESSONS_PATH = "lessons";

    //MMEDIA
    public static final String M_APP_DIRECTORY = "Clapp";



}
