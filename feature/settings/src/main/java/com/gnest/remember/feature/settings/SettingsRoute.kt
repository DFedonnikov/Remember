package com.gnest.remember.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    SettingsScreen(modifier)
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
}
