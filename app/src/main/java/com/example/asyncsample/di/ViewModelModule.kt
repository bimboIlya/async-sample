package com.example.asyncsample.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.asyncsample.MyViewmodel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @VMKey(MyViewmodel::class)
    abstract fun getMyViewModel(vm: MyViewmodel): ViewModel

    @Binds
    abstract fun getVMFactory(vmFactory: ViewModelFactory): ViewModelProvider.Factory
}


@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class VMKey(val value: KClass<out ViewModel>)


@Singleton
class ViewModelFactory @Inject constructor(
    private val vmMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        vmMap[modelClass]?.let {
            @Suppress("UNCHECKED_CAST")
            return it.get() as T
        }

        throw IllegalArgumentException("Unknown viewmodel $modelClass")
    }
}