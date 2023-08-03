package com.gnest.remember.feature.permissionprovider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gnest.remember.core.common.extensions.findActivity
import com.gnest.remember.core.designsystem.icon.RememberIcons
import com.gnest.remember.core.designsystem.icon.asIcon
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.permission.Permission
import com.gnest.remember.core.ui.dialogs.ModalDialog
import com.gnest.remember.feature.permissionprovider.navigation.PermissionScreen
import com.gnest.remember.feature.permissions.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun PermissionDialog(viewModel: PermissionScreenViewModel = hiltViewModel()) {
    val state = viewModel.state.collectAsStateWithLifecycle(null).value
    if (state != null) {
        val context = LocalContext.current
        val permissionState = rememberPermissionState(permission = state.permission.description) { isGranted ->
            if (isGranted) {
                viewModel.onDismiss()
            } else {
                val isNotShowingRationale =
                    !ActivityCompat.shouldShowRequestPermissionRationale(requireNotNull(context.findActivity()), state.permission.description)
                if (isNotShowingRationale) {
                    viewModel.onPermissionPermanentlyDenied()
                }
            }
        }
        LaunchedEffect(key1 = permissionState.status.isGranted) {
            if (permissionState.status.isGranted) {
                viewModel.onDismiss()
            }
        }
        when (state) {
            is ScreenState.PermissionPermanentlyDenied -> PermanentlyDeniedPermissionDialog(state, viewModel)
            is ScreenState.RequestPermission -> RequestPermissionDialog(state, permissionState, viewModel)
        }
    }
}

@Composable
private fun PermanentlyDeniedPermissionDialog(
    state: ScreenState.PermissionPermanentlyDenied,
    viewModel: PermissionScreenViewModel
) {
    val context = LocalContext.current
    val packageName = remember { context.packageName }
    ModalDialog(
        title = state.title,
        text = state.text,
        icon = RememberIcons.SettingsOutlined.asIcon,
        approveButtonText = TextSource.Resource(R.string.settings),
        dismissButtonText = TextSource.Resource(R.string.dismiss),
        onApprove = { viewModel.openApplicationSettings(packageName) },
        onDismiss = { viewModel.onDismiss() },
        onDismissRequest = { viewModel.onDismiss() })
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RequestPermissionDialog(
    state: ScreenState.RequestPermission,
    permissionState: PermissionState,
    viewModel: PermissionScreenViewModel
) {
    val text = when {
        permissionState.status.shouldShowRationale -> state.permissionExplanation
        else -> state.text
    }
    ModalDialog(
        title = state.title,
        text = text,
        icon = RememberIcons.ShieldExclamationOutlined.asIcon,
        approveButtonText = TextSource.Resource(R.string.approve),
        dismissButtonText = TextSource.Resource(R.string.dismiss),
        onApprove = { permissionState.launchPermissionRequest() },
        onDismiss = { viewModel.onDismiss() },
        onDismissRequest = { viewModel.onDismiss() }
    )
}

internal val SavedStateHandle.permissionFlow: Flow<Permission>
    get() = getStateFlow(PermissionScreen.permissionArg, null).filterNotNull()