package com.example.asyncsample.di

import android.app.Application
import com.example.asyncsample.MyApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        MainActivityModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent {
    @Component.Builder
    interface Builder {

        fun build(): AppComponent

        @BindsInstance
        fun application(app: Application): Builder
    }

    fun inject(app: MyApplication)
}