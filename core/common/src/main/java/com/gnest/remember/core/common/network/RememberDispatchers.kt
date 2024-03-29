package com.gnest.remember.core.common.network

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val rememberDispatchers: RememberDispatchers)

enum class RememberDispatchers {
    IO,
    SINGLE
}