package com.construapp.construapp.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.construapp.construapp.models.Comment;
import com.construapp.construapp.models.Lesson;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by ESTEBANFML on 12-11-2017.
 */

@Dao
public interface CommentDAO {

    @Query("select * from Comment")
    List<Comment> getAllComments();

    @Query("select * from Comment where lessonId = :lessonId order by id ASC")
    List<Comment> getAllLessonComments(String lessonId);

    @Query("select * from Comment where id = :id")
    Comment getCommentById(String id);

    @Query("SELECT COUNT(*) FROM Comment")
    int commentCount();

    @Insert(onConflict = REPLACE)
    void insertComment(Comment comment);

    @Delete
    void deleteComment(Comment comment);

    @Query("DELETE FROM Comment")
    void nukeTable();

}
