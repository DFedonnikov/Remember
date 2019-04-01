package com.gnest.remember.di

import android.app.Application
import com.gnest.remember.di.modules.DataModule
import com.gnest.remember.presentation.view_model.ViewModelsFactory
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {

        fun build(): AppComponent

        @BindsInstance
        fun application(application: Application): Builder
    }

    val viewModelsFactory: ViewModelsFactory
}