package com.example.asyncsample.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton


@Singleton
class ViewModelFactory @Inject constructor(
    private val vmMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        vmMap[modelClass]?.let {
            @Suppress("UNCHECKED_CAST")
            return it.get() as T
        }

        throw IllegalArgumentException("unknown viewmodel $modelClass")
    }
}