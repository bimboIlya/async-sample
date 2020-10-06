package com.example.asyncsample.model.sources

import android.annotation.SuppressLint
import com.example.asyncsample.model.Comment
import com.example.asyncsample.model.Post
import com.example.asyncsample.model.User
import io.reactivex.Observable
import io.reactivex.Scheduler
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

// retrofit client works faster with RxJava than with coroutines
@Singleton
@SuppressLint("CheckResult")  // subscriptions w/out reference get GC'ed on completion
class RepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val dao: MyDao,
    private val schedulerIO: Scheduler,
    private val dispatcherIO: CoroutineDispatcher
) : Repository {

    override suspend fun getUsersDb(): List<User> = withContext(dispatcherIO) {
        dao.getUsers()
    }

    override suspend fun getPostsDb(): List<Post> = withContext(dispatcherIO) {
        dao.getUsersWithPosts()
            .flatMap { it.posts }
    }

    override suspend fun getCommentsDb(): List<Comment> = withContext(dispatcherIO) {
        dao.getUsersWithPostsWithComments()
            .flatMap { it.posts }
            .flatMap { it.comments }
    }

    // -------------------- RxJava section --------------------

    override fun getUserStreamRx(): Observable<User> {
        return api.getAllUsersRx()
            .subscribeOn(schedulerIO)
            .doOnNext { insertUsersRx(it) }
            .flatMapIterable { it }
    }

    override fun getPostStreamRx(): Observable<Post> {
        return getUserStreamRx()
            .subscribeOn(schedulerIO)
            .flatMap {
                api.getPostsByUserIdRx(it.id)
                    .subscribeOn(schedulerIO)
            }
            .doOnNext { insertPostsRx(it) }
            .flatMapIterable { it }
    }

    override fun getCommentStreamRx(): Observable<Comment> {
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


    // -------------------- Coroutines section --------------------

    override suspend fun getUsersSus(): List<User> = withContext(dispatcherIO) {
        try {
            return@withContext api.getAllUsersSus()
                .also { dao.insertUsersSus(it) }
        } catch (e: Exception) {
            Timber.d(e)
            throw RepositoryException()
        }
    }

    override suspend fun getPostsSus(): List<Post> = withContext(dispatcherIO) {
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

    override suspend fun getCommentsSus(): List<Comment> = withContext(dispatcherIO) {
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

    override suspend fun getPostsChannels(): List<Post> = withContext(dispatcherIO) {
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

    override suspend fun getCommentsChannels(): List<Comment> = withContext(dispatcherIO) {
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

    // flow runs on a single coroutine
    // network requests perform on the same thread on the same coroutine
    // so when request is sent, coroutine suspends itself, once it gets response,
    // it continues, sends next request and so on, so it takes a lot of time
    // need to find a way to launch coroutine for every request
    @FlowPreview
    override suspend fun getUsersFlow(): Flow<User> {
        return api.getAllUsersFlow()
            .onEach { dao.insertUsersSus(it) }
            .flatMapConcat { it.asFlow() }
            .flowOn(dispatcherIO)
    }

    @FlowPreview
    override suspend fun getPostsFlow(): Flow<Post> {
        return getUsersFlow()
            .flatMapConcat { api.getPostsByUserIdFlow(it.id) }  // sync
            .onEach { dao.insertPostsSus(it) }
            .flatMapConcat { it.asFlow() }
            .flowOn(dispatcherIO)
    }

    @FlowPreview
    override suspend fun getCommentsFlow(): Flow<Comment> {
        return getPostsFlow()
            .flatMapConcat { api.getCommentsByPostIdFlow(it.id) }  // sync
            .onEach { dao.insertCommentsSus(it) }
            .flatMapConcat { it.asFlow() }
            .flowOn(dispatcherIO)
    }
}