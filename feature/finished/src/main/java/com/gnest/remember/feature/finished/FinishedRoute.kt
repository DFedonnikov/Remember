package com.gnest.remember.feature.finished

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun FinishedRoute(modifier: Modifier = Modifier,
                           viewModel: FinishedViewModel = hiltViewModel()) {
    FinishedScreen(modifier)
}

@Composable
internal fun FinishedScreen(modifier: Modifier = Modifier) {

}
