package com.gnest.remember.core.navigation.di

import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.core.navigation.NavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    @Singleton
    fun bindNavigator(navigator: NavigatorImpl): Navigator
}