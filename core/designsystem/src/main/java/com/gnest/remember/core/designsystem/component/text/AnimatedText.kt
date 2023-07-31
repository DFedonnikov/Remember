package com.gnest.remember.core.designsystem.component.text

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationVector3D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.designsystem.theme.asString

@Composable
fun AnimatedText(
    text: TextSource,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null
) {
    val animatedStyle by animateValueAsState(targetValue = style, typeConverter =
    TwoWayConverter(
        {
            AnimationVector3D(
                it.fontSize.value,
                it.fontWeight?.weight?.toFloat() ?: 400f,
                it.lineHeight.value

            )
        }, { vector ->
            style.copy(
                fontSize = vector.v1.sp,
                fontWeight = vector.v2.toInt().takeIf { it > 0 }?.let { FontWeight(it) } ?: style.fontWeight,
                lineHeight = vector.v3.sp
            )
        })
    )
    val animatedColor by animateColorAsState(targetValue = color)
    Text(
        modifier = modifier,
        text = text.asString,
        style = animatedStyle,
        color = animatedColor,
        textAlign = textAlign,
        textDecoration = textDecoration
    )
}