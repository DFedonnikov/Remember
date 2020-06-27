package com.gnest.remember.extensions

import android.view.View

fun View.setPaddingOnly(left: Int = paddingLeft, top: Int = paddingTop, right: Int = paddingRight, bottom: Int = paddingBottom) {
    setPadding(left, top, right, bottom)
}