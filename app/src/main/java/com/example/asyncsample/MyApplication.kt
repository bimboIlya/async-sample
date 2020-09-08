package com.example.asyncsample

import android.app.Application
import androidx.viewbinding.BuildConfig
import com.example.asyncsample.model.Repository
import com.example.asyncsample.model.buildDb
import com.example.asyncsample.model.getService
import timber.log.Timber

class MyApplication : Application() {

    val repository by lazy {
        Repository(
            getService(),
            buildDb(this).myDao
        )
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}