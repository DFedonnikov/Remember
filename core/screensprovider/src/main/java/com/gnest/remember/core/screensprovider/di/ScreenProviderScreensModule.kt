package com.gnest.remember.core.screensprovider.di

import com.gnest.remember.core.screensprovider.AppModuleScreenDependency
import com.gnest.remember.core.screensprovider.AppModuleScreenDependencyImpl
import com.gnest.remember.core.screensprovider.InterestingIdeaScreenDependencyImpl
import com.gnest.remember.core.screensprovider.NewNoteScreenDependencyImpl
import com.gnest.remember.core.screensprovider.NoteSettingsScreenDependencyImpl
import com.gnest.remember.core.screensprovider.ReminderScreenDependencyImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.gnest.remember.feature.interestingidea.navigation.ScreenDependency as InterestingIdeaScreenDependency
import com.gnest.remember.feature.newnote.navigation.ScreenDependency as NewNoteScreenDependency
import com.gnest.remember.feature.notesettings.navigation.ScreenDependency as NoteSettingsScreenDependency
import com.gnest.remember.feature.reminder.navigation.ScreenDependency as ReminderScreenDependency

@Module
@InstallIn(SingletonComponent::class)
internal interface ScreenProviderScreensModule {

    @Binds
    fun bindAppModuleScreenDependency(impl: AppModuleScreenDependencyImpl): AppModuleScreenDependency

    @Binds
    fun bindNewNoteScreenDependency(impl: NewNoteScreenDependencyImpl): NewNoteScreenDependency

    @Binds
    fun bindInterestingIdeaScreenDependency(impl: InterestingIdeaScreenDependencyImpl): InterestingIdeaScreenDependency

    @Binds
    fun bindNoteSettingsScreenDependency(impl: NoteSettingsScreenDependencyImpl): NoteSettingsScreenDependency

    @Binds
    fun bindReminderScreenDependency(impl: ReminderScreenDependencyImpl): ReminderScreenDependency

}