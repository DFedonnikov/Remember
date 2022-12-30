package com.gnest.remember.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gnest.remember.core.designsystem.component.text.TextSMRegular
import com.gnest.remember.core.designsystem.component.text.TextXLBold
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.TextSource

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
internal fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    interestingNotes: @Composable () -> Unit
) {
    val hasAnyNotes by viewModel.hasAnyNotes.collectAsStateWithLifecycle(false)
    when {
        hasAnyNotes -> NotesList(modifier, interestingNotes)
        else -> EmptyScreen(modifier)
    }
}

@Composable
private fun NotesList(modifier: Modifier = Modifier, interestingNotes: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = 24.dp)
    ) {
        interestingNotes()
    }
}

@Composable
@Preview(widthDp = 360, heightDp = 780, backgroundColor = 0xFFF9FFFB, showBackground = true)
internal fun EmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxHeight(0.15f)
                .fillMaxWidth()
        )
        Image(
            modifier = Modifier.fillMaxWidth(0.75F),
            painter = painterResource(id = R.drawable.ic_home_empty),
            contentScale = ContentScale.FillWidth,
            contentDescription = "Empty home screen image"
        )
        TextXLBold(
            text = TextSource.Resource(R.string.start_your_journey),
            textAlign = TextAlign.Center
        )
        TextSMRegular(
            text = TextSource.Resource(R.string.empty_screen_description),
            textAlign = TextAlign.Center,
            color = AppColors.Topaz
        )
        Image(
            modifier = Modifier.padding(bottom = 24.dp),
            painter = painterResource(id = R.drawable.ic_arrow),
            contentDescription = "arrow"
        )

    }
}