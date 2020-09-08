package com.example.asyncsample.model

import android.content.Context
import androidx.room.*

@Database(
    entities = [User::class, Post::class, Comment::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract val myDao: MyDao
}

fun buildDb(context: Context): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, "db").build()
}

// TODO
@Dao
interface MyDao {
    @Query("SELECT * FROM Post")
    suspend fun getPosts(): List<Post>
}