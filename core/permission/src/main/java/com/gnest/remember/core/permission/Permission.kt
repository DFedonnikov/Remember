package com.gnest.remember.core.permission

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed interface Permission : Parcelable {

    val description: String

    @Parcelize
    @Serializable
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    object PostNotifications : Permission {


        override val description: String get() = android.Manifest.permission.POST_NOTIFICATIONS
    }
}