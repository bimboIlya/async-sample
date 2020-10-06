package com.example.asyncsample.model.sources

import com.example.asyncsample.model.Comment
import com.example.asyncsample.model.Post
import com.example.asyncsample.model.User
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

interface Repository {

    suspend fun getUsersDb(): List<User>

    suspend fun getPostsDb(): List<Post>

    suspend fun getCommentsDb(): List<Comment>



    fun getUserStreamRx(): Observable<User>

    fun getPostStreamRx(): Observable<Post>

    fun getCommentStreamRx(): Observable<Comment>



    suspend fun getUsersSus(): List<User>

    suspend fun getPostsSus(): List<Post>

    suspend fun getCommentsSus(): List<Comment>



    suspend fun getPostsChannels(): List<Post>

    suspend fun getCommentsChannels(): List<Comment>



    suspend fun getUsersFlow(): Flow<User>

    suspend fun getPostsFlow(): Flow<Post>

    suspend fun getCommentsFlow(): Flow<Comment>
}


class RepositoryException(
    message: String? = null,
    cause: Throwable? = null
) : Throwable(message, cause)