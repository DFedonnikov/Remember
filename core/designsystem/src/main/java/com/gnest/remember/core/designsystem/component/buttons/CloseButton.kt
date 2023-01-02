package com.gnest.remember.core.designsystem.component.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gnest.remember.core.designsystem.icon.RememberIcons
import com.gnest.remember.core.designsystem.theme.AppColors

@Composable
fun CloseButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val contentColor by animateColorAsState(
        targetValue = when {
            isPressed -> AppColors.FrenchGray
            else -> AppColors.AthensGray

        }
    )
    IconButton(
        modifier = modifier
            .clip(CircleShape)
            .background(contentColor)
            .size(24.dp),
        onClick = onClick,
        enabled = isEnabled,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = contentColor,
            disabledContentColor = AppColors.AthensGray,
        ),
        interactionSource = interactionSource
    ) {
        val tint by animateColorAsState(targetValue = if (isEnabled) AppColors.Topaz else AppColors.FrenchGray)
        Icon(
            painter = painterResource(id = RememberIcons.Close),
            tint = tint,
            contentDescription = "Close button"
        )
    }
}

@Preview
@Composable
private fun CloseButtonPreview() {
    CloseButton { }
}