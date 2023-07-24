package com.gnest.remember.feature.reminder.time

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
import com.gnest.remember.core.ui.pickers.TimePicker
import com.gnest.remember.feature.reminder.R

@Composable
internal fun ReminderTime(
    modifier: Modifier = Modifier,
    viewModel: ReminderTimeViewModel = hiltViewModel()
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
                isBackEnabled = state.isBackEnabled,
                onBackClick = { viewModel.onBackClick() },
                onCloseClick = { viewModel.onCloseClick() })
            Divider(
                modifier = Modifier.padding(top = 16.dp),
                thickness = 0.5.dp,
                color = AppColors.AthensGray
            )
            TimePicker(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                time = state.selectedTime
            ) { viewModel.onTimeChanged(it) }
        }
    }
}