package com.construapp.construapp.models;

/**
 * Created by ESTEBANFML on 18-10-2017.
 */

public interface Constants {

    // General
    String BASE_URL = "http://18.231.22.248";
    String COMPANIES = "companies";
    String MICROBLOG = "microblogs";
    String SECTIONS = "sections";
    String THREADS = "threads";
    String COMMENTS = "comments";
    String POSTS = "posts";
    String POST = "Post";
    String LESSONS = "lessons";
    String ATTRIBUTES = "attributes";
    String USERS = "users";
    String GET_PROJECTS = "get_projects";
    String GET_PERMISSION = "get_permission";
    String GET_USERS = "get_users";
    String ASSIGN_VALIDATOR = "assign_validator";
    String PENDING_VALIDATIONS = "pending_validations";
    String PROJECTS = "projects";
    String SESSIONS = "sessions";
    String SAVE_KEY = "save_key";
    String FAVOURITES = "favourites";
    String ALL_PROJECTS_KEY = "null";
    String ALL_PROJECTS_NAME = "Todos los proyectos";
    String SEARCH = "search";
    String PATTERN = "pattern";

    String API_VALIDATE = "validate";
    String API_SEND = "send";
    String API_REJECT = "reject";

    String R_REJECTED = "-1";
    String R_WAITING = "0";
    String R_VALIDATED = "1";
    String R_SAVED = "2";


    // Shared preferences
    String SP_CONSTRUAPP = "ConstruApp";
    String SP_COMPANY = "company_id";
    String SP_USER = "user_id";
    String SP_ADMIN = "admin";
    String SP_TOKEN = "token";
    String SP_HAS_PROJECTS = "has_projects";
    String SP_HAS_PERMISSION = "has_permission";
    String SP_PROJECTS = "projects";
    String SP_ACTUAL_PROJECT = "actual_project";
    String SP_ACTUAL_PROJECT_NAME = "actual_project_name";
    String SP_USER_PERMISSION_NAME = "name_permission";
    String SP_USER_PERMISSION = "user_permission";
    String SP_PERMISSION_PROJECT = "permission_project_";
    String SP_ACTUAL_SECTION = "actual_section";
    String SP_HAS_PENDING_VALIDATIONS = "has_pending_validations";
    String SP_PENDING_VALIDATIONS = "pending_validations";
    String SP_FAVOURITE_LESSONS = "favourite_lessons";
    String SP_THREAD_ID = "thread_id";
    String SP_DISCIPLINES = "disciplines";
    String SP_CLASSIFICATIONS = "classifications";
    String SP_DEPARTMENTS = "departments";
    String SP_TAGS = "tags";

    //Queries
    String Q_AUTHORIZATION = "Authorization";
    String Q_CONTENTTYPE = "Content-Type";
    String Q_CONTENTTYPE_JSON = "application/json";
    String Q_CONTENTTYPE_JSON_UTF8 = "application/json; charset=utf-8";

    //S3
    String S3_BUCKET = "construapp";
    String S3_IMAGES_PATH = "images";
    String S3_VIDEOS_PATH = "videos";
    String S3_AUDIOS_PATH = "audios";
    String S3_DOCS_PATH = "docs";
    String S3_LESSONS_PATH = "lessons";

    //MMEDIA
    String M_APP_DIRECTORY = "Clapp";

    //SESSION
    String S_ADMIN_ADMIN = "1";
    String S_ADMIN_NOTADMIN = "0";
    String S_ADMIN_SUPERADMIN = "2";

    //PERMISSION
    String P_READ = "1";
    String P_CREATE = "2";
    String P_VALIDATE = "3";
    String P_ADMIN = "4";

    //BUNDLE FRAGMENTS
    String B_LESSON_ARRAY_LIST = "lesson_array_list";
    String B_LESSON_REJECT_COMMENT = "lesson_reject_comment";
    String B_SECTION_ARRAY_LIST = "sections_array_list";
    String B_LESSON_COMMENTS = "lesson_comment";
    String B_LESSON_ID = "lesson_id";

    //MESSAGES
    String NO_ATTACHMENTS = "NO EXISTEN ARCHIVOS ADJUNTOS";
    String NO_AUDIOS = "No existen audios";
    String NO_VIDEOS = "No existen videos";
    String NO_DOCUMENTS = "No existen documentos";
    String NO_PICTURES = "No existen imagenes";

    //TITLES
    String TITLE_FAVOURITE_LESSONS = "Lecciones favoritas";

    //IMAGES
    String IMAGE_ICC_PROFILE = "ICC Profile";

    //TAGS
    String TAG_TAGS = "TAG_TAGS";
    String TAG_DISCIPLINES = "TAG_DISCIPLINES";
    String TAG_CLASSIFICATIONS = "TAG_CLASSIFICATIONS";
    String TAG_DEPARTMENTS = "TAG_DEPARTMENTS";

}
