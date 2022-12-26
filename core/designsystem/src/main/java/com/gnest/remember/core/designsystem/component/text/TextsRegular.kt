package com.gnest.remember.core.designsystem.component.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.designsystem.theme.asString

@Composable
fun TextSMRegular(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = AppColors.Ebony,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun TextXSRegular(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = AppColors.Ebony,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = MaterialTheme.typography.bodySmall,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun Text2XSRegular(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = AppColors.Ebony,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = MaterialTheme.typography.bodySmall,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}