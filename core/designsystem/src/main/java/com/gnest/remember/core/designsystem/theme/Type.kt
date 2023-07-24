package com.gnest.remember.core.designsystem.theme

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Remember Typography
 */

internal val RememberTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    //Verified
    headlineLarge = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize = 32.sp,
        lineHeight = 38.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    //Verified
    headlineSmall = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 24.sp,
        lineHeight = 28.8.sp
    ),
    //Verified
    titleLarge = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize = 24.sp,
        lineHeight = 28.sp
    ),
    //Verified
    titleMedium = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    //Verified
    titleSmall = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    //Verified
    bodyLarge = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp
    ),
    //Verified
    bodyMedium = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    //Verified
    bodySmall = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    //Verified
    labelLarge = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    //Verified
    labelMedium = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 12.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.W500,
        fontSize = 10.sp,
        lineHeight = 16.sp
    )
)

sealed interface TextSource {
    class Simple(val text: String) : TextSource
    class Resource(@StringRes val resId: Int) : TextSource

    class Formatted(val source: TextSource, vararg args: Any) : TextSource {

        val arguments = args
    }

    class StringArray(
        @ArrayRes val array: Int,
        val indexes: List<Int>,
        val separator: CharSequence = ", "
    ) : TextSource
}

val TextSource.asString: String
    @Composable get() = when (this) {
        is TextSource.Simple -> text
        is TextSource.Resource -> stringResource(id = resId)
        is TextSource.Formatted -> source.asString.format(args = arguments)
        is TextSource.StringArray -> {
            val stringArray = stringArrayResource(id = array)
            indexes.joinToString(separator = separator) { stringArray[it] }
        }
    }

val String.asTextSource get() = TextSource.Simple(this)