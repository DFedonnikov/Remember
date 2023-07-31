package com.gnest.remember.feature.reminder.approve

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gnest.remember.core.designsystem.icon.RememberIcons
import com.gnest.remember.core.designsystem.icon.asIcon
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.ui.dialogs.ModalDialog
import com.gnest.remember.feature.reminder.R

@Composable
fun ApproveDialog(viewModel: ApproveScreenViewModel = hiltViewModel()) {
    val state = viewModel.state.collectAsStateWithLifecycle(initialValue = null).value
    if (state != null) {
        ModalDialog(
            icon = RememberIcons.CheckOutlined.asIcon,
            title = state.title,
            text = state.text,
            approveButtonText = TextSource.Resource(R.string.ok),
            dismissButtonText = TextSource.Resource(R.string.dismiss),
            onApprove = { viewModel.onApproveClick() },
            onDismiss = { viewModel.onDismissClick() },
            onDismissRequest = { viewModel.onDismissClick() }
        )
    }

}