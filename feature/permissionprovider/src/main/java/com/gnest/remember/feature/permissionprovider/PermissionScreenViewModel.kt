package com.gnest.remember.feature.permissionprovider

import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.core.permission.Permission
import com.gnest.remember.feature.permissions.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
internal class PermissionScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val navigator: Navigator
) : ViewModel() {

    private var isPermissionPermanentlyDenied = MutableStateFlow(false)

    internal val state = savedStateHandle.permissionFlow.combine(isPermissionPermanentlyDenied) { permission, isPermanentlyDenied ->
        when {
            isPermanentlyDenied -> ScreenState.PermissionPermanentlyDenied(
                title = permission.title,
                text = permission.deniedPermissionText,
                permission = permission
            )
            else -> ScreenState.RequestPermission(
                title = permission.title,
                text = permission.text,
                permissionExplanation = permission.explanation,
                permission = permission
            )
        }
    }

    private val Permission.title
        get():  TextSource = when (this) {
            Permission.PostNotifications -> TextSource.Resource(R.string.post_notifications_title)
        }

    private val Permission.text
        get() : TextSource = when (this) {
            Permission.PostNotifications -> TextSource.Resource(R.string.post_notifications_text)
        }

    private val Permission.explanation
        get() : TextSource = when (this) {
            Permission.PostNotifications -> TextSource.Resource(R.string.post_notifications_explanation)
        }

    private val Permission.deniedPermissionText
        get():  TextSource = when (this) {
            Permission.PostNotifications -> TextSource.Resource(R.string.post_notifications_denied_text)
        }

    fun onDismiss() {
        navigator.popBack()
    }

    fun openApplicationSettings(packageName: String) {
        navigator.openExternalActivity(
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            data = Uri.parse("package:$packageName")
        )
    }

    fun onPermissionPermanentlyDenied() {
        isPermissionPermanentlyDenied.tryEmit(true)
    }


}

internal sealed interface ScreenState {

    val permission: Permission

    data class RequestPermission(
        val title: TextSource,
        val text: TextSource,
        val permissionExplanation: TextSource,
        override val permission: Permission
    ) : ScreenState

    data class PermissionPermanentlyDenied(
        val title: TextSource,
        val text: TextSource,
        override val permission: Permission
    ) : ScreenState
}