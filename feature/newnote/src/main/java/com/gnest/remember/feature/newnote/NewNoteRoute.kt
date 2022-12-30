package com.gnest.remember.feature.newnote

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gnest.remember.core.designsystem.icon.Icon
import com.gnest.remember.core.designsystem.icon.RememberIcons
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.RememberTheme
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.designsystem.theme.asString
import com.gnest.remember.core.ui.DevicePreviews
import com.gnest.remember.core.ui.NoteTypeCard
import com.gnest.remember.core.ui.TopBar

@Composable
internal fun NewNoteRoute(
    modifier: Modifier = Modifier,
    viewModel: NewNoteViewModel = hiltViewModel(),
) {
    NewNoteScreen(
        modifier = modifier.fillMaxSize(),
        navigateToInterestingIdea = { viewModel.openInterestingIdea() },
        onBackClick = { viewModel.onBackClick() })
}

@Composable
private fun NewNoteScreen(
    modifier: Modifier = Modifier,
    navigateToInterestingIdea: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        TopBar(title = TextSource.Resource(R.string.new_note_title), onBackClick = onBackClick)
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .padding(top = 24.dp, bottom = 16.dp),
        ) {
            Text(
                text = TextSource.Resource(R.string.new_note_header).asString,
                style = MaterialTheme.typography.titleLarge
            )
            NoteTypeCard(
                modifier = Modifier.padding(top = 32.dp),
                backgroundColor = AppColors.Jewel,
                icon = Icon.DrawableResourceIcon(RememberIcons.Idea),
                title = TextSource.Resource(R.string.interesting_idea_title),
                subtitle = TextSource.Resource(R.string.interesting_idea_subtitle),
                onClick = navigateToInterestingIdea
            )
            NoteTypeCard(
                modifier = Modifier.padding(top = 24.dp),
                backgroundColor = AppColors.VistaBlue,
                icon = Icon.DrawableResourceIcon(RememberIcons.Cart),
                title = TextSource.Resource(R.string.buying_something_title),
                subtitle = TextSource.Resource(R.string.buying_something_subtitle)
            ) {}
            NoteTypeCard(
                modifier = Modifier.padding(top = 24.dp),
                backgroundColor = AppColors.LightningYellow,
                icon = Icon.DrawableResourceIcon(RememberIcons.Goals),
                title = TextSource.Resource(R.string.goals_title),
                subtitle = TextSource.Resource(R.string.goals_subtitle)
            ) {}
            NoteTypeCard(
                modifier = Modifier.padding(top = 24.dp),
                backgroundColor = AppColors.BrickRed,
                icon = Icon.DrawableResourceIcon(RememberIcons.Guidance),
                title = TextSource.Resource(R.string.guidance_title),
                subtitle = TextSource.Resource(R.string.guidance_subtitle)
            ) {}
            NoteTypeCard(
                modifier = Modifier.padding(top = 24.dp),
                backgroundColor = AppColors.RoyalPurple,
                icon = Icon.DrawableResourceIcon(RememberIcons.RoutineTasks),
                title = TextSource.Resource(R.string.routine_tasks_title),
                subtitle = TextSource.Resource(R.string.routine_subtasks_subtitle)
            ) {}
        }
    }
}


@Composable
@DevicePreviews
private fun NewNoteScreenPreview() {
    RememberTheme {
        NewNoteScreen(navigateToInterestingIdea = {}, onBackClick = {})
    }
}