package com.gnest.remember.feature.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun SearchRoute(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    SearchScreen(modifier)
}

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
}
