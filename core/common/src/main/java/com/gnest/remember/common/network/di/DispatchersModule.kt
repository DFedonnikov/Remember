package com.gnest.remember.common.network.di

import com.gnest.remember.common.network.Dispatcher
import com.gnest.remember.common.network.RememberDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Dispatcher(RememberDispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @OptIn(DelicateCoroutinesApi::class)
    @Provides
    @Dispatcher(RememberDispatchers.SINGLE)
    fun providesSingleDispatcher(): CoroutineDispatcher = newSingleThreadContext("SingleThread")

}
