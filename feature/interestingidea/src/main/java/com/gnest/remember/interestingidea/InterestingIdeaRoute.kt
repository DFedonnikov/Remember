package com.gnest.remember.interestingidea

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gnest.remember.core.designsystem.component.FreeAreaTextField
import com.gnest.remember.core.designsystem.component.TitleTextField
import com.gnest.remember.core.designsystem.theme.RememberTheme
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.ui.DevicePreviews
import com.gnest.remember.core.ui.TopBar

@Composable
internal fun InterestingIdeaRoute(
    modifier: Modifier = Modifier,
    viewModel: InterestingIdeaViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val state = viewModel.state.collectAsState(initial = null).value
    if (state != null) {
        InterestingIdeaScreen(
            modifier = modifier,
            state = state,
            onTitleChanged = { viewModel.updateTitle(it) },
            onTextChanged = { viewModel.updateText(it) },
            onBackClick = onBackClick
        )
    }
}

@Composable
private fun InterestingIdeaScreen(
    modifier: Modifier = Modifier,
    state: InterestingIdeaState,
    onTitleChanged: (String) -> Unit,
    onTextChanged: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Column(modifier = modifier) {
        TopBar(onBackClick = onBackClick)
        TitleTextField(
            modifier = Modifier
                .padding(top = 24.dp)
                .padding(horizontal = 13.dp),
            value = state.title,
            placeholderText = TextSource.Resource(com.gnest.remember.interestingidea.R.string.title_placeholder),
            onValueChange = onTitleChanged
        )
        FreeAreaTextField(
            modifier = Modifier.padding(horizontal = 13.dp),
            value = state.text,
            placeholderText = TextSource.Resource(R.string.text_placeholder),
            onValueChange = onTextChanged
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
            onBackClick = {})
    }
}