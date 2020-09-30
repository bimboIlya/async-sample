package com.example.asyncsample.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.google.gson.annotations.SerializedName

@Entity
data class User(
    @PrimaryKey val id: Long,
    val name: String,
    val username: String,
    val email: String,
    @Embedded val address: Address,
    val phone : String,
    val website : String,
    @Embedded val company : Company
)

data class Address (
    val street : String,
    val suite : String,
    val city : String,
    val zipcode : String,
    @Embedded val geo : Geo
)

data class Geo (
    val lat : Double,
    val lng : Double
)

data class Company (
    @SerializedName("name") val companyName : String,
    val catchPhrase : String,
    val bs : String
)

@Entity
data class Post(
    val userId: Long,
    @PrimaryKey val id: Long,
    val title: String,
    val body: String
)

@Entity
data class Comment(
    val postId: Long,
    @PrimaryKey val id: Long,
    val name: String,
    val email: String,
    val body: String,
)

data class UserWithPosts(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val posts: List<Post>
)

data class PostsWithComments(
    @Embedded val post: Post,
    @Relation(
        parentColumn = "postId",
        entityColumn = "id"
    )
    val comments: List<Comment>
)

data class UsersWithPostsWithComments(
    @Embedded val user: User,
    @Relation(
        entity = Post::class,
        parentColumn = "userId",
        entityColumn = "id"
    )
    val posts: List<PostsWithComments>
)