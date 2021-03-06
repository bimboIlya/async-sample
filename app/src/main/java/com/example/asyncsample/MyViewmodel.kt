package com.example.asyncsample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asyncsample.AsyncOption.*
import com.example.asyncsample.DataOption.*
import com.example.asyncsample.model.sources.Repository
import com.example.asyncsample.model.sources.RepositoryException
import com.example.asyncsample.util.subscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

enum class AsyncOption {
    RXJAVA,
    COROUTINES,
    CHANNELS,
    FLOW,
    DATABASE
}

enum class DataOption {
    USER,
    POST,
    COMMENT
}

class MyViewmodel @Inject constructor(private val repo: Repository) : ViewModel() {

    private val _chosenAsyncOption = MutableLiveData(RXJAVA)
    val chosenAsyncOption: LiveData<AsyncOption> = _chosenAsyncOption

    private val _chosenDataOption = MutableLiveData(POST)
    val chosenDataOption: LiveData<DataOption> = _chosenDataOption

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _result = MutableLiveData("")
    val result: LiveData<String> = _result

    private val _message = MutableLiveData<String?>(null)
    val message: LiveData<String?> = _message

    private val cd = CompositeDisposable()

    private var startTime: Long = 0
    private var finishTime: Long = 0


    fun setAsyncOption(option: AsyncOption) {
        _chosenAsyncOption.postValue(option)
    }

    fun setDataOption(option: DataOption) {
        _chosenDataOption.postValue(option)
    }

    fun messageShown() {
        _message.value = null
    }

    fun load() {
        when (chosenAsyncOption.value) {
            RXJAVA -> { rx() }
            COROUTINES -> { coroutines() }
            CHANNELS -> { channels() }
            FLOW -> { flow() }
            DATABASE -> { db() }
        }
    }

    private fun rx() {
        when (chosenDataOption.value!!) {
            USER -> { loadRx { repo.getUserStreamRx() } }
            POST -> { loadRx { repo.getPostStreamRx() } }
            COMMENT -> { loadRx { repo.getCommentStreamRx() } }
        }
    }

    private fun coroutines() {
        when (chosenDataOption.value!!) {
            USER -> { loadCoroutines(COROUTINES) { repo.getUsersSus() } }
            POST -> { loadCoroutines(COROUTINES) { repo.getPostsSus() } }
            COMMENT -> { loadCoroutines(COROUTINES) { repo.getCommentsSus() } }
        }
    }

    private fun channels() {
        when (chosenDataOption.value!!) {
            USER -> { loadCoroutines(CHANNELS) { repo.getUsersSus() } }
            POST -> { loadCoroutines(CHANNELS) { repo.getPostsChannels() } }
            COMMENT -> { loadCoroutines(CHANNELS) { repo.getCommentsChannels() } }
        }
    }

    private fun flow() {
        when (chosenDataOption.value!!) {
            USER -> { loadFlow { repo.getUsersFlow() } }
            POST -> { loadFlow { repo.getPostsFlow() } }
            COMMENT -> { loadFlow { repo.getCommentsFlow() } }
        }
    }

    private fun db() {
        when (chosenDataOption.value!!) {
            USER -> { loadCoroutines(DATABASE) { repo.getUsersDb() } }
            POST -> { loadCoroutines(DATABASE) { repo.getPostsDb() } }
            COMMENT -> { loadCoroutines(DATABASE) { repo.getCommentsDb() } }
        }
    }

    private fun <T : Any> loadRx(func: () -> Observable<T>) {
        func()
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onLoadingStarted() }
            .subscribe(cd,
                { onLoadingSuccess(RXJAVA, it) },
                { onLoadingFailed(it) }
            )
    }

    private fun <T : Any> loadCoroutines(option: AsyncOption, func: suspend () -> List<T>) {
        viewModelScope.launch {
            onLoadingStarted()

            try {
                val result = func()
                onLoadingSuccess(option, result)
            } catch (e: RepositoryException) {
                onLoadingFailed(e)
            }
        }
    }

    private fun <T : Any> loadFlow(func: suspend () -> Flow<T>) {
        viewModelScope.launch {
            val result = func()
                .onStart { onLoadingStarted() }
                .catch {
                    onLoadingFailed(it)
                    cancel()
                }
                .toList()

            if (isActive) onLoadingSuccess(FLOW, result)
        }
    }

    private fun displayDownloadedData(
        option: AsyncOption,
        className: String,
        amount: Int,
        timeMs: Long,
    ) {
        val currentResult = "$option: Downloaded $amount $className in $timeMs ms\n"
        _result.value = _result.value!! + currentResult
    }

    private fun onLoadingStarted() {
        _isLoading.value = true
        startTime = System.currentTimeMillis()
    }

    private fun onLoadingStopped() {
        _isLoading.value = false
        finishTime = System.currentTimeMillis()
    }

    private fun <T : Any> onLoadingSuccess(option: AsyncOption, data: List<T>) {
        onLoadingStopped()
        if (!data.isNullOrEmpty()) {
            displayDownloadedData(
                option,
                data.first()::class.java.simpleName,
                data.size,
                finishTime - startTime
            )
        } else {
           _message.value = "List is empty"
        }
    }

    private fun onLoadingFailed(t: Throwable) {
        onLoadingStopped()
        _message.value = "eror"
        Timber.d(t)
    }

    override fun onCleared() {
        cd.dispose()
    }
}