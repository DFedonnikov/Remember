package com.gnest.remember.core.ui

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.noRippleClickable(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    indication: Indication? = null,
    onClick: () -> Unit
): Modifier = composed {
    clickable(
        indication = indication,
        interactionSource = remember { interactionSource },
        onClick = onClick
    )
}