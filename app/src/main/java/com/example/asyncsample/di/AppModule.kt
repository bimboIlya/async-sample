package com.example.asyncsample.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.asyncsample.model.sources.ApiService
import com.example.asyncsample.model.sources.AppDatabase
import com.example.asyncsample.model.sources.BASE_URL
import com.example.asyncsample.model.sources.MyDao
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun getService(): ApiService {
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun getDb(context: Application): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "db").build()
    }

    @Singleton
    @Provides
    fun getDao(db: AppDatabase): MyDao = db.myDao()
}