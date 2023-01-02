package com.gnest.remember.interestingidea

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gnest.remember.core.designsystem.component.text.FreeAreaTextField
import com.gnest.remember.core.designsystem.component.text.TitleTextField
import com.gnest.remember.core.designsystem.theme.RememberTheme
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.ui.DevicePreviews
import com.gnest.remember.core.ui.NewNoteTaskBar
import com.gnest.remember.core.ui.TopBar

@Composable
internal fun InterestingIdeaRoute(
    modifier: Modifier = Modifier,
    viewModel: InterestingIdeaViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState(initial = InterestingIdeaState())
    InterestingIdeaScreen(
        modifier = modifier.fillMaxSize(),
        state = state,
        onTitleChanged = { viewModel.updateTitle(it) },
        onTextChanged = { viewModel.updateText(it) },
        onBackClick = { viewModel.onBackClick() },
        onMoreClicked = { viewModel.onMoreClick() }
    )
}

@Composable
private fun InterestingIdeaScreen(
    modifier: Modifier = Modifier,
    state: InterestingIdeaState,
    onTitleChanged: (String) -> Unit,
    onTextChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    onMoreClicked: () -> Unit,
) {
    val backgroundColor by animateColorAsState(targetValue = state.color)
    Column(modifier = modifier
        .background(backgroundColor)
        .navigationBarsPadding()) {
        TopBar(onBackClick = onBackClick)
        TitleTextField(
            modifier = Modifier
                .padding(top = 24.dp)
                .padding(horizontal = 13.dp),
            value = state.title,
            placeholderText = TextSource.Resource(R.string.title_placeholder),
            onValueChange = onTitleChanged
        )
        FreeAreaTextField(
            modifier = Modifier
                .padding(horizontal = 13.dp)
                .weight(1f),
            value = state.text,
            placeholderText = TextSource.Resource(R.string.text_placeholder),
            onValueChange = onTextChanged
        )
        NewNoteTaskBar(
            lastEditedDate = state.lastEdited,
            onSearchClicked = { },
            onPinClicked = { },
            onMoreClicked = onMoreClicked
        )
    }
}

@Composable
@DevicePreviews
private fun InterestingIdeaScreenPreview() {
    RememberTheme {
        InterestingIdeaScreen(
            state = InterestingIdeaState(),
            onTitleChanged = {},
            onTextChanged = {},
            onBackClick = {},
            onMoreClicked = {})
    }
}