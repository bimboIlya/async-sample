package com.example.asyncsample.model.sources

import androidx.room.*
import com.example.asyncsample.model.Comment
import com.example.asyncsample.model.Post
import com.example.asyncsample.model.User
import io.reactivex.Completable

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsersRx(users: List<User>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsersSus(users: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPostsRx(posts: List<Post>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostsSus(posts: List<Post>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCommentsRx(comments: List<Comment>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommentsSus(comments: List<Comment>)
}