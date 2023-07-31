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
fun TextXSBold(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = MaterialTheme.typography.labelLarge,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun Text2XSBold(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = TextStyles.Text2XSBold,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun TextSMBold(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = MaterialTheme.typography.bodyLarge,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun TextBaseBold(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = MaterialTheme.typography.titleLarge,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun TextLgBold(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = TextStyles.TextLgBold,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun TextXLBold(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = MaterialTheme.typography.headlineLarge,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun Text2XLBold(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = TextStyles.Text2XLBold,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}

@Composable
fun Text3XLBold(
    modifier: Modifier = Modifier,
    text: TextSource,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    Text(
        modifier = modifier,
        text = text.asString,
        style = MaterialTheme.typography.displayLarge,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}