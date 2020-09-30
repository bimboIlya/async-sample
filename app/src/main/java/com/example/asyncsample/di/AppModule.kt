package com.example.asyncsample.di

import android.app.Application
import androidx.room.Room
import com.example.asyncsample.model.sources.*
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
object AppModule {

    @Singleton
    @Provides
    fun getService(client: OkHttpClient): ApiService {
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(BASE_URL)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun getClient(interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Singleton
    @Provides
    fun getInterceptor(application: Application): Interceptor {
        return FakeNetworkInterceptor(application)
    }

    @Singleton
    @Provides
    fun getDb(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "db"
        ).build()
    }

    @Singleton
    @Provides
    fun getDao(db: AppDatabase): MyDao = db.myDao()

    @Singleton
    @Provides
    fun getSchedulerIO() = Schedulers.io()

    @Singleton
    @Provides
    fun getDispatchersIO() = Dispatchers.IO
}