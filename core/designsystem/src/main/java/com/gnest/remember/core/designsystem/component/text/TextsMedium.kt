package com.gnest.remember.core.designsystem.component.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.designsystem.theme.TextStyles
import com.gnest.remember.core.designsystem.theme.asString

@Composable
fun TextXSMedium(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun Text2XSMedium(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = TextStyles.Text2XLMedium,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun TextSMMedium(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = MaterialTheme.colorScheme.primary,
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
fun TextBaseMedium(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = MaterialTheme.typography.titleMedium,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun TextLgMedium(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = TextStyles.TextLgMedium,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun TextXLMedium(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = MaterialTheme.typography.headlineMedium,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun Text2XLMedium(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = TextStyles.Text2XLMedium,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun Text3XLMedium(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = MaterialTheme.typography.displayMedium,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}