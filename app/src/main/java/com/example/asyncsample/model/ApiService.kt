package com.example.asyncsample.model

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

const val BASE_URL = "https://jsonplaceholder.typicode.com/"

// TODO
interface ApiService {

    @GET("users")
    fun getAllUsers(): List<User>

    @GET("users/{userId}/posts")
    fun getPostsByUserId(@Path("userId") userId: Long): List<Post>

    @GET("users/{userId}/posts")
    fun getCommentsByPostId(@Path("postId") postId: Long): List<Comment>

    @GET("users")
    fun getAllUsersRx(): Observable<List<User>>

    @GET("users/{userId}/posts")
    fun getPostsByUserIdRx(@Path("userId") userId: Long): Observable<List<Post>>

    @GET("posts/{postId}/comments")
    fun getCommentsByPostIdRx(@Path("postId") postId: Long): Observable<List<Comment>>
}

fun getService(): ApiService {
    val retrofit = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    return retrofit.create(ApiService::class.java)
}