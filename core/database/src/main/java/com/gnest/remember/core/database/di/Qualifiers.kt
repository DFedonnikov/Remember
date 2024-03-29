package com.gnest.remember.core.database.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainRealm

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ArchivedRealm