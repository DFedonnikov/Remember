package com.gnest.remember.core.designsystem.component.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.designsystem.theme.TextStyles
import com.gnest.remember.core.designsystem.theme.asString

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
        style = MaterialTheme.typography.labelSmall,
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
        style = TextStyles.Text2XSRegular,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

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
        style = MaterialTheme.typography.bodySmall,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun TextBaseRegular(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = AppColors.Ebony,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = MaterialTheme.typography.titleSmall,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun TextLgRegular(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = AppColors.Ebony,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = TextStyles.TextLgRegular,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun TextXLRegular(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = AppColors.Ebony,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = MaterialTheme.typography.headlineSmall,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun Text2XLRegular(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = AppColors.Ebony,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = TextStyles.Text2XLRegular,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun Text3XLRegular(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = AppColors.Ebony,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = MaterialTheme.typography.displaySmall,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}