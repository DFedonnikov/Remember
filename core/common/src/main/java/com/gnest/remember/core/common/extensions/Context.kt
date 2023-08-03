package com.gnest.remember.core.common.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> {
            baseContext.findActivity()
        }
        else -> null
    }
}