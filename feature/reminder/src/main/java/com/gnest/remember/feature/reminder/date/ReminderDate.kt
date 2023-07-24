package com.gnest.remember.feature.reminder.date

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gnest.remember.core.designsystem.component.BottomSheetHeader
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.ui.pickers.DatePicker
import com.gnest.remember.feature.reminder.R

@Composable
internal fun ReminderDate(
    modifier: Modifier = Modifier,
    viewModel: ReminderDateViewModel = hiltViewModel()
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        val state = viewModel.state.collectAsStateWithLifecycle(null).value
        if (state != null) {
            BottomSheetHeader(
                modifier = Modifier.padding(top = 16.dp),
                title = TextSource.Resource(R.string.set_reminder),
                onBackClick = { viewModel.onBackClick() },
                onCloseClick = { viewModel.onCloseClick() })
            Divider(
                modifier = Modifier.padding(top = 16.dp),
                thickness = 0.5.dp,
                color = AppColors.AthensGray
            )
            DatePicker(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                dateUiState = state.dateUiState
            ) { viewModel.onDateChanged(it) }
        }
    }
}