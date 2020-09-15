package com.example.asyncsample.model.sources

import com.example.asyncsample.model.Comment
import com.example.asyncsample.model.Post
import com.example.asyncsample.model.User
import io.reactivex.Observable
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