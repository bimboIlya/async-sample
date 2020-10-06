package com.example.asyncsample.model.sources

import androidx.room.*
import com.example.asyncsample.model.*
import io.reactivex.Completable

@Database(
    entities = [User::class, Post::class, Comment::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun myDao(): MyDao
}

@Dao
interface MyDao {
    @Query("SELECT * FROM User")
    suspend fun getUsers(): List<User>

    @Transaction
    @Query("SELECT * FROM User")
    suspend fun getUsersWithPosts(): List<UserWithPosts>

    @Transaction
    @Query("SELECT * FROM User")
    suspend fun getUsersWithPostsWithComments(): List<UsersWithPostsWithComments>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsersSus(users: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommentsSus(comments: List<Comment>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostsSus(posts: List<Post>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsersRx(users: List<User>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPostsRx(posts: List<Post>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCommentsRx(comments: List<Comment>): Completable
}