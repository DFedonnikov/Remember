package com.gnest.remember.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val LightColorScheme = lightColorScheme(
    primary = AppColors.Jewel,
    background = AppColors.HintOfGreen,
    onPrimary = AppColors.White,
    onPrimaryContainer = AppColors.Jewel
)

@Composable
fun RememberTheme(content: @Composable () -> Unit) {
    val colorScheme = LightColorScheme
    MaterialTheme(colorScheme = colorScheme, content = content)
}
