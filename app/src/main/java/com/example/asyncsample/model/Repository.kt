package com.example.asyncsample.model

import io.reactivex.Observable

class Repository(
    val api: ApiService,
    val dao: MyDao
) {
    suspend fun getPosts(): List<Post> = TODO()

    suspend fun getComments(): List<Comment> = TODO()

    fun getPostsRx(): Observable<List<Post>> = TODO()

    fun getCommentsRx(): Observable<List<Comment>> = TODO()
}