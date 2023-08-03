package com.gnest.remember.feature.notesettings

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gnest.remember.core.designsystem.component.buttons.CloseButton
import com.gnest.remember.core.designsystem.component.text.TextXSRegular
import com.gnest.remember.core.designsystem.icon.Icon
import com.gnest.remember.core.designsystem.icon.RememberIcons
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.permission.Permission
import com.gnest.remember.core.ui.lists.ColorSelector
import com.gnest.remember.core.ui.menuitems.ArrowLabelMenuItem
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun NoteSettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: NoteSettingsViewModel = hiltViewModel()
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        CloseButton(
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 16.dp)
        ) { viewModel.onCloseClick() }
        TextXSRegular(
            modifier = Modifier.padding(top = 8.dp),
            text = TextSource.Resource(R.string.change_background),
            color = AppColors.Topaz
        )
        val state by viewModel.state.collectAsStateWithLifecycle(initialValue = ScreenState(emptyList(), TextSource.Resource(R.string.not_set)))
        ColorSelector(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            items = state.backgroundColors,
            onItemClick = { viewModel.onItemClick(it) })
        Divider(
            modifier = Modifier.padding(top = 16.dp),
            thickness = 0.5.dp,
            color = AppColors.AthensGray
        )
        TextXSRegular(
            modifier = Modifier.padding(top = 16.dp),
            text = TextSource.Resource(R.string.extras_upper_case),
            color = AppColors.Topaz
        )
        val permissionState = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> rememberPermissionState(permission = Permission.PostNotifications.description)
            else -> null
        }
        ArrowLabelMenuItem(
            modifier = Modifier.padding(top = 8.dp, bottom = 200.dp),
            icon = Icon.DrawableResourceIcon(RememberIcons.ClockOutlined),
            title = TextSource.Resource(R.string.set_reminder),
            extra = state.dateTime
        ) {
            viewModel.onSetReminderClicked(permissionState?.status?.isGranted ?: true)
        }
    }
}