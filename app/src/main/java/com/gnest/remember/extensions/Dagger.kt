package com.gnest.remember.extensions

import com.gnest.remember.di.AppComponent

fun <T> inject(func: AppComponent.() -> T) = lazy { appComponent.func() }