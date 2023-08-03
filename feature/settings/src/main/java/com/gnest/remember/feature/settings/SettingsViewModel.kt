package com.gnest.remember.feature.settings

import androidx.lifecycle.ViewModel
import com.gnest.remember.feature.settings.navigation.SettingsScreensProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val provider: SettingsScreensProvider) : ViewModel() {

}