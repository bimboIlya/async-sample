package com.example.asyncsample.model.sources

import android.app.Application
import android.net.ConnectivityManager
import com.example.asyncsample.model.Comment
import com.example.asyncsample.model.Post
import com.example.asyncsample.model.User
import com.google.gson.Gson
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.*
import retrofit2.http.GET
import retrofit2.http.Path
import timber.log.Timber
import java.io.IOException

// real url
//const val BASE_URL = "https://jsonplaceholder.typicode.com/"
const val BASE_URL = "https://localhost"
const val NETWORK_DELAY = 800L


interface ApiService {

    @GET("users")
    suspend fun getAllUsersSus(): List<User>

    @GET("users/{userId}/posts")
    suspend fun getPostsByUserIdSus(@Path("userId") userId: Long): List<Post>

    @GET("posts/{postId}/comments")
    suspend fun getCommentsByPostIdSus(@Path("postId") postId: Long): List<Comment>


    @GET("users")
    fun getAllUsersRx(): Observable<List<User>>

    @GET("users/{userId}/posts")
    fun getPostsByUserIdRx(@Path("userId") userId: Long): Observable<List<Post>>

    @GET("posts/{postId}/comments")
    fun getCommentsByPostIdRx(@Path("postId") postId: Long): Observable<List<Comment>>
}

// Retrofit doesn't allow functions without annotations inside service interface

 suspend fun ApiService.getAllUsersFlow(): Flow<List<User>> = flow {
    emit(getAllUsersSus())
}

 suspend fun ApiService.getPostsByUserIdFlow(userId: Long): Flow<List<Post>> = flow {
    emit(getPostsByUserIdSus(userId))
}

 suspend fun ApiService.getCommentsByPostIdFlow(postId: Long): Flow<List<Comment>> = flow {
    emit(getCommentsByPostIdSus(postId))
}


/**
 * Returns Response to Retrofit without accessing the Internet
 *
 * If BASE_URL is actual url, it will proceed unchanged, otherwise
 * it would get same files locally
 */
class FakeNetworkInterceptor(
    private val application: Application,
    private val shouldLog: Boolean = false
) : Interceptor {
    private val gson = Gson()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        logRequest(request)

        if (BASE_URL.contains("jsonplaceholder.typicode.com")) {
            return chain.proceed(request)
                .also { logResponse(it) }
        }

        if (!hasInternetConnection()) return makeErrorResponse("No Internet", request)

        Thread.sleep(NETWORK_DELAY)  // pretend making network request

        val fileName = request
            .url()
            .uri()
            .toString()
            .drop(BASE_URL.length + 1)  // client add '/' at the end of base url
            .replace('/', '-')
            .run {
                return@run if (!endsWith(".json")) "$this.json" else this
            }

//        log("Assets file name: $fileName")

        return try {
            application.assets.open(fileName).use { inputStream ->
                makeSuccessResponse(inputStream.reader().readText(), request)
            }
        } catch (e: IOException) {
            makeErrorResponse("No such file", request)
        }
    }

    private fun makeSuccessResponse(jsonBody: String, request: Request): Response {
        val response = Response.Builder()
            .code(200)
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .message("Success")
            .body(ResponseBody.create(
                MediaType.get("application/json"),
                jsonBody))
            .build()

        logResponse(response)

        return response
    }

    private fun makeErrorResponse(message: String, request: Request): Response {
        val response =  Response.Builder()
            .code(500)
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .message(message)
            .body(ResponseBody.create(
                MediaType.get("application/json"),
                gson.toJson("something wrong idk")))
            .build()

        logResponse(response)

        return response
    }

    @Suppress("DEPRECATION")
    private fun hasInternetConnection(): Boolean {
        val cm =
            application.getSystemService(Application.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        return activeNetwork?.isConnectedOrConnecting ?: false
    }

    // DON'T ever log response body, otherwise it would be empty
    private fun logResponse(response: Response) {
        with(response) {
            log("<-- ${code()} ${request().url()} ${message()}" +
                    " ${receivedResponseAtMillis() - sentRequestAtMillis()}ms") // 0ms if fake response
        }
    }

    private fun logRequest(request: Request) {
        with(request) {
            log("--> ${method()} ${url()}")
        }
    }

    private fun log(message: String) {
        if (shouldLog) {
            Timber.tag(LOG_TAG).i(message)
        }
    }


    companion object {
        const val LOG_TAG = "Interceptor"
    }
}