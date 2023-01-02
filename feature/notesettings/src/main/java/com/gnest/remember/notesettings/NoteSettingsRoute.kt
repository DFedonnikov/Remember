package com.gnest.remember.notesettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gnest.remember.core.designsystem.component.buttons.CloseButton
import com.gnest.remember.core.designsystem.component.text.TextXSRegular
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.ui.lists.ColorSelector

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NoteSettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: NoteSettingsViewModel = hiltViewModel()
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        CloseButton(
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 16.dp)
        ) { viewModel.onCloseClick() }
        TextXSRegular(
            modifier = Modifier.padding(top = 8.dp),
            text = TextSource.Resource(R.string.change_background),
            color = AppColors.Topaz
        )
        val items by viewModel.backgroundColors.collectAsStateWithLifecycle(initialValue = emptyList())
        ColorSelector(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 200.dp)
                .fillMaxWidth(),
            items = items,
            onItemClick = { viewModel.onItemClick(it) })
    }
}