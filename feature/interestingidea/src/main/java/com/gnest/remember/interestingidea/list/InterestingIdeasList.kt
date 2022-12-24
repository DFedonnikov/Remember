package com.gnest.remember.interestingidea.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.ui.lists.ListNote
import com.gnest.remember.core.ui.lists.NotesRowList
import com.gnest.remember.interestingidea.R

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
internal fun InterestingIdeasRowList(
    modifier: Modifier = Modifier,
    viewModel: InterestingIdeasListViewModel = hiltViewModel()
) {
    val interestingIdeas by viewModel.notes.collectAsStateWithLifecycle(initialValue = emptyList())
    NotesRowList(
        modifier = modifier,
        listTitle = TextSource.Resource(R.string.interesting_idea),
        notesList = interestingIdeas
    )
}

internal data class InterestingIdeaUi(
    val id: Long,
    override val title: TextSource = TextSource.Simple(""),
    override val text: TextSource = TextSource.Simple(""),
    override val color: Color = AppColors.White
) : ListNote