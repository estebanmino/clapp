package com.construapp.construapp.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Query;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Delete;

import com.construapp.construapp.models.Lesson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @autho - Jose
 @versión - hoy

 */

@Dao
public interface LessonDAO {

    @Query("select * from Lesson")
    public List<Lesson> getAllLessons();

    //no usamos livedata porque la db se sincroniza segun el fetch
    //LiveData<List<Lesson>> getAllLessons();

    @Query("select * from Lesson where id = :id")
    Lesson getLessonById(String id);

    @Query("select * from Lesson where user_id = :id")
    List<Lesson> getLessonByUserId(String id);


    @Query("select * from Lesson where project_id = :id")
    List<Lesson> getLessonByProjectId(String id);

    @Query("select * from Lesson where (project_id = :projectId) AND (user_id = :userId)")
    List<Lesson> getLessonByUserAndProjectId(String userId, String projectId);

    @Query("select * from Lesson where (project_id = :projectId) AND(validation = 2) AND (validator = :validatorValue)")
    List<Lesson> getLessonByProjectIdAndValidator(String projectId,String validatorValue);

    @Query("select * from Lesson where (validation = 2) AND (validator = :validatorValue)")
    List<Lesson> getLessonByValidator(String validatorValue);

    @Query("SELECT COUNT(*) FROM Lesson")
    int lessonCount();

    @Insert(onConflict = REPLACE)
    void insertLesson(Lesson Lesson);

    @Delete
    void deleteLesson(Lesson Lesson);

    @Query("DELETE FROM Lesson")
    public void nukeTable();
}


