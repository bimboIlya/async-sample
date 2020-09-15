package com.example.asyncsample.di

import android.app.Application
import android.content.Context
import com.example.asyncsample.MainActivity
import com.example.asyncsample.MyApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AppModule::class,
    MainActivityModule::class
])
interface AppComponent {
    @Component.Builder
    interface Builder {

        fun build(): AppComponent

        @BindsInstance
        fun application(app: Application): Builder
    }

     fun inject(app: MyApplication)
}