package com.gnest.remember.feature.reminder.repeat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gnest.remember.core.designsystem.component.BottomSheetHeader
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.ui.listitems.ChoiceListItemWidget
import com.gnest.remember.core.ui.listitems.SimpleListItemWidget
import com.gnest.remember.feature.reminder.R
import com.gnest.remember.feature.reminder.RepeatPeriodUi

@Composable
internal fun ReminderRepeat(
    modifier: Modifier = Modifier,
    viewModel: ReminderRepeatViewModel = hiltViewModel()
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
            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                itemsIndexed(state.days) { index, item ->
                    when (item) {
                        is RepeatPeriodUi.Custom -> ChoiceListItemWidget(
                            item = item,
                            isSelected = index == state.selectedIndex,
                            onItemClick = { viewModel.onChangeCustomPeriodClick() },
                            onChangeClick = { viewModel.onChangeCustomPeriodClick() }
                        )
                        else -> SimpleListItemWidget(
                            item = item,
                            isSelected = index == state.selectedIndex,
                            textRepresentation = { it.title },
                            onItemClick = { viewModel.onItemSelected(it) }
                        )
                    }
                }
            }
        }
    }
}