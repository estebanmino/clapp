package com.construapp.construapp.db;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.arch.persistence.room.Room;

import com.construapp.construapp.models.Lesson;

/**
 * Created by jose on 10-10-17.
 */



@Database(entities = {Lesson.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user_db")
                            .build();
        }
        return INSTANCE;
    }

    public abstract LessonDAO lessonDAO();
}
