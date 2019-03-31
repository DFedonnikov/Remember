package com.gnest.remember.extensions

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.toast(@StringRes textResId: Int, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, textResId, length).show()