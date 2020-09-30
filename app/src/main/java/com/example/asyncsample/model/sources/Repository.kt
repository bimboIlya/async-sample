package com.example.asyncsample.model.sources

import android.annotation.SuppressLint
import com.example.asyncsample.model.Comment
import com.example.asyncsample.model.Post
import com.example.asyncsample.model.User
import io.reactivex.Observable
import io.reactivex.Scheduler
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

// for some reason, retrofit client works faster with RxJava than coroutines
@Singleton
@SuppressLint("CheckResult")  // subscriptions w/out reference get GC'ed on completion
class Repository @Inject constructor(
    private val api: ApiService,
    private val dao: MyDao,
    private val schedulerIO: Scheduler,
    private val dispatcherIO: CoroutineDispatcher
) {

    // -------------------- Coroutines section --------------------

    suspend fun getUsersSus(): List<User> = withContext(dispatcherIO) {
        try {
            return@withContext api.getAllUsersSus()
                .also { dao.insertUsersSus(it) }
        } catch (e: Exception) {
            Timber.d(e)
            throw RepositoryException()
        }
    }

    suspend fun getPostsSus(): List<Post> = withContext(dispatcherIO) {
        try {
            val deferredList: List<Deferred<List<Post>>> = getUsersSus()
                .map { user ->
                    async { api.getPostsByUserIdSus(user.id) }
                }

            return@withContext deferredList.awaitAll()
                .flatten()
                .also { dao.insertPostsSus(it) }
        } catch (e: Exception) {
            Timber.d(e)
            throw RepositoryException()
        }
    }

    suspend fun getCommentsSus(): List<Comment> = withContext(dispatcherIO) {
        try {
            val deferredList: List<Deferred<List<Comment>>> = getPostsSus()
                .map { post ->
                    async { api.getCommentsByPostIdSus(post.id) }
                }

            return@withContext deferredList.awaitAll()
                .flatten()
                .also { dao.insertCommentsSus(it) }
        } catch (e: Exception) {
            Timber.d(e)
            throw RepositoryException()
        }
    }


    // -------------------- Channels section --------------------

    suspend fun getPostsChannels(): List<Post> = withContext(dispatcherIO) {
        try {
            val channel = Channel<List<Post>>()
            val users = getUsersSus()

            users.forEach { user ->
                launch {
                    val singleUserPosts = api.getPostsByUserIdSus(user.id)
                    channel.send(singleUserPosts)
                }
            }

            val result = mutableListOf<Post>()

            repeat(users.size) {
                val posts = channel.receive()
                result.addAll(posts)
            }

            dao.insertPostsSus(result)

            return@withContext result
        } catch (e: Exception) {
            Timber.d(e)
            throw RepositoryException()
        }
    }

    suspend fun getCommentsChannels(): List<Comment> = withContext(dispatcherIO) {
        try {
            val channel = Channel<List<Comment>>()
            val comments = getPostsChannels()

            comments.forEach { post ->
                launch {
                    val singlePostComments = api.getCommentsByPostIdSus(post.id)
                    channel.send(singlePostComments)
                }
            }

            val result = mutableListOf<Comment>()

            repeat(comments.size) {
                val posts = channel.receive()
                result.addAll(posts)
            }

            dao.insertCommentsSus(result)

            return@withContext result
        } catch (e: Exception) {
            Timber.d(e)
            throw RepositoryException()
        }
    }


    // -------------------- Flow section --------------------

    suspend fun getUsersFlow(): Flow<User> {
        TODO()
    }

    suspend fun getPostsFlow(): Flow<Post> {
        TODO()
    }


    suspend fun getCommentsFlow(): Flow<Comment> {
        TODO()
    }


    // -------------------- RxJava section --------------------

       fun getUserStreamRx(): Observable<User> {
        return api.getAllUsersRx()
            .subscribeOn(schedulerIO)
            .doOnNext { insertUsersRx(it) }
            .flatMapIterable { it }
    }

    fun getPostStreamRx(): Observable<Post> {
        return getUserStreamRx()
            .subscribeOn(schedulerIO)
            .flatMap {
                api.getPostsByUserIdRx(it.id)
                    .subscribeOn(schedulerIO)
            }
            .doOnNext { insertPostsRx(it) }
            .flatMapIterable { it }
    }

    fun getCommentStreamRx(): Observable<Comment> {
        return getPostStreamRx()
            .subscribeOn(schedulerIO)
            .flatMap {
                api.getCommentsByPostIdRx(it.id)
                    .subscribeOn(schedulerIO)
            }
            .doOnNext { insertCommentsRx(it) }
            .flatMapIterable { it }
    }

    private fun insertUsersRx(users: List<User>) {
        dao.insertUsersRx(users)
            .subscribeOn(schedulerIO)
            .subscribe()
    }

    private fun insertPostsRx(posts: List<Post>) {
        dao.insertPostsRx(posts)
            .subscribeOn(schedulerIO)
            .subscribe()
    }

    private fun insertCommentsRx(comments: List<Comment>) {
        dao.insertCommentsRx(comments)
            .subscribeOn(schedulerIO)
            .subscribe()
    }
}


class RepositoryException(
    message: String? = null,
    cause: Throwable? = null
) : Throwable(message, cause)