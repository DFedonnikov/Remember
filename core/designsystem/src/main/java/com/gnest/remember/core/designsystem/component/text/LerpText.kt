package com.gnest.remember.core.designsystem.component.text

import  androidx.annotation.FloatRange
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.lerp
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.designsystem.theme.asString

@Composable
fun LerpText(
    modifier: Modifier = Modifier,
    text: TextSource,
    @FloatRange(0.0, 1.0) ratio: Float,
    startColor: Color,
    endColor: Color = startColor,
    startFontSize: TextUnit,
    endFontSize: TextUnit = startFontSize,
    startFontWeight: FontWeight,
    endFontWeight: FontWeight = startFontWeight,
    startLineHeight: TextUnit,
    endLineHeight: TextUnit = startLineHeight,
    textAlign: TextAlign? = null
) {
    val currentColor = lerp(startColor, endColor, ratio)
    val fontSize = lerp(startFontSize, endFontSize, ratio)
    val fontWeight = lerp(startFontWeight, endFontWeight, ratio)
    val lineHeight = lerp(startLineHeight, endLineHeight, ratio)
    Text(
        modifier = modifier,
        text = text.asString,
        color = currentColor,
        fontSize = fontSize,
        fontWeight = fontWeight,
        lineHeight = lineHeight,
        textAlign = textAlign
    )
}