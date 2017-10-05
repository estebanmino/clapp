package com.construapp.construapp.models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.TypeConverters;
import java.util.List;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Query;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Delete;

/**
 * Created by jose on 05-10-17.
 */
@Dao
@TypeConverters(Lesson.class)
public interface LessonDAO {

    @Query("select * from Lesson")
    LiveData<List<Lesson>> getAllLessons();

    @Query("select * from Lesson where id = :id")
    Lesson getLessonbyId(String id);

    @Insert(onConflict = REPLACE)
    void addLesson(Lesson Lesson);

    @Delete
    void deleteLesson(Lesson Lesson);
}
