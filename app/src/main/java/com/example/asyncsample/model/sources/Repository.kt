package com.example.asyncsample.model.sources

import com.example.asyncsample.model.Comment
import com.example.asyncsample.model.Post
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    val api: ApiService,
    val dao: MyDao
) {
    suspend fun getPosts(): List<Post> = TODO()

    suspend fun getComments(): List<Comment> = TODO()

    fun getPostsRx(): Observable<List<Post>> = TODO()

    fun getCommentsRx(): Observable<List<Comment>> = TODO()
}