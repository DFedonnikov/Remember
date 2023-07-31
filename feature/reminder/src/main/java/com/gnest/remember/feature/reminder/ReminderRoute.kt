package com.gnest.remember.feature.reminder

import androidx.activity.compose.BackHandler
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
import com.gnest.remember.core.ui.menuitems.ArrowLabelMenuItem
import com.gnest.remember.core.ui.menuitems.SwitchMenuItem

@Composable
internal fun ReminderRoute(
    modifier: Modifier = Modifier,
    viewModel: ReminderRouteViewModel = hiltViewModel()
) {
    BackHandler { viewModel.onBackClick() }
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        BottomSheetHeader(
            modifier = Modifier.padding(top = 16.dp),
            title = TextSource.Resource(R.string.extras),
            onBackClick = { viewModel.onBackClick() },
            onCloseClick = { viewModel.onCloseClick() })
        Divider(
            modifier = Modifier.padding(top = 16.dp),
            thickness = 0.5.dp,
            color = AppColors.AthensGray
        )
        val state = viewModel.state.collectAsStateWithLifecycle(initialValue = null).value
        if (state != null) {
            SwitchMenuItem(
                title = TextSource.Resource(R.string.reminder),
                isChecked = state.isReminderEnabled,
                onCheckChanged = { viewModel.onReminderChanged(it) }
            )
            ArrowLabelMenuItem(
                title = TextSource.Resource(R.string.date),
                extra = state.date,
                isEnabled = state.isReminderEnabled
            ) { viewModel.onDateChangeClicked() }
            ArrowLabelMenuItem(
                title = TextSource.Resource(R.string.time),
                extra = state.time,
                isEnabled = state.isReminderEnabled
            ) { viewModel.onTimeChangeClicked() }
            ArrowLabelMenuItem(
                title = TextSource.Resource(R.string.repeat),
                extra = state.repeat,
                isEnabled = state.isReminderEnabled
            ) { viewModel.onRepeatClicked() }
        }

    }
}