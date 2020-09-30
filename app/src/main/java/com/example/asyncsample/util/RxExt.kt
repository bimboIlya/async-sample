package com.example.asyncsample.util

import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

fun <T> Single<T>.subscribe(
    cd: CompositeDisposable,
    onSuccess: Consumer<in T>,
    onError: Consumer<in Throwable>
) {
    val subscription = this.subscribe(onSuccess, onError)
    cd.add(subscription)
}