package com.construapp.construapp.models;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @autho - Jose
 @versión - hoy

 */

@Dao
@TypeConverters(Lesson.class)
public interface LessonDAO {

    @Query("select * from Lesson")
    public ArrayList<Lesson> getAllLessons();

    //no usamos livedata porque la db se sincroniza segun el fetch
    //LiveData<List<Lesson>> getAllLessons();

    @Query("select * from Lesson where id = :id")
    Lesson getLessonbyId(String id);

    @Insert(onConflict = REPLACE)
    void addLesson(Lesson Lesson);

    @Delete
    void deleteLesson(Lesson Lesson);
}


