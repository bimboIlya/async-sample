package com.example.asyncsample.model.sources

import android.content.Context
import androidx.room.*
import com.example.asyncsample.model.Comment
import com.example.asyncsample.model.Post
import com.example.asyncsample.model.User

@Database(
    entities = [User::class, Post::class, Comment::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun myDao(): MyDao
}

// TODO
@Dao
interface MyDao {
    @Query("SELECT * FROM Post")
    suspend fun getPosts(): List<Post>
}