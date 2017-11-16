package com.construapp.construapp.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.construapp.construapp.models.Comment;
import com.construapp.construapp.models.Lesson;

/**
 * Created by ESTEBANFML on 12-11-2017.
 */

@Database(entities = {Comment.class}, version = 1, exportSchema = false)
public abstract class CommentDatabase extends RoomDatabase {

    private static CommentDatabase INSTANCE;

    public static CommentDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), CommentDatabase.class, "comment_db")
                            .build();
        }
        return INSTANCE;
    }

    public abstract CommentDAO commentDAO();
}