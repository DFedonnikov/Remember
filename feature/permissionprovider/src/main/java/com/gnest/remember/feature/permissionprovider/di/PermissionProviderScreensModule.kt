package com.gnest.remember.feature.permissionprovider.di

import com.gnest.remember.feature.permissionprovider.navigation.PermissionScreensProvider
import com.gnest.remember.feature.permissionprovider.navigation.PermissionScreensProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface PermissionProviderScreensModule {

    @Binds
    fun bindScreensProvider(provider: PermissionScreensProviderImpl): PermissionScreensProvider
}