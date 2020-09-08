package com.example.asyncsample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.asyncsample.AsyncOption.*

enum class AsyncOption {
    RXJAVA,
    COROUTINES,
    FLOW
}

class MyViewmodel : ViewModel() {
    private val _chosenAsyncOption = MutableLiveData(RXJAVA)
    val chosenAsyncOption: LiveData<AsyncOption> = _chosenAsyncOption

    fun setAsyncOption(option: AsyncOption) {
        _chosenAsyncOption.postValue(option)
    }

    fun load() {
        when (_chosenAsyncOption.value!!) {
            RXJAVA -> {  }
            COROUTINES -> {  }
            FLOW -> {  }
        }
    }
}