package com.example.asyncsample.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val id: Long,
    val name: String,
    val username: String,
    val email: String,
//    val address: Address,
//    val phone : String,
//    val website : String,
//    val company : Company
)

data class Address (
    val street : String,
    val suite : String,
    val city : String,
    val zipcode : String,
    val geo : Geo
)

data class Geo (
    val lat : Double,
    val lng : Double
)

data class Company (
    val name : String,
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