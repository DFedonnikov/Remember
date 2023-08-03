package com.gnest.remember.core.designsystem.theme

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Remember Typography
 */

internal object TextStyles {

    val TextXSRegular = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.W400)
    val TextXSMedium = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.W500)
    val TextXSBold = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.W700)
    val Text2XSRegular = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.W400)
    val Text2XSMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.W500)
    val Text2XSBold = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.W700)
    val TextSMRegular = TextStyle(
        fontSize = 14.sp,
        lineHeight = 19.6.sp,
        fontWeight = FontWeight.W400
    )
    val TextSMMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 19.6.sp,
        fontWeight = FontWeight.W500
    )
    val TextSMBold = TextStyle(
        fontSize = 14.sp,
        lineHeight = 19.6.sp,
        fontWeight = FontWeight.W700
    )
    val TextBaseRegular = TextStyle(
        fontSize = 16.sp,
        lineHeight = 22.4.sp,
        fontWeight = FontWeight.W400
    )
    val TextBaseMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 22.4.sp,
        fontWeight = FontWeight.W500
    )
    val TextBaseBold = TextStyle(
        fontSize = 16.sp,
        lineHeight = 22.4.sp,
        fontWeight = FontWeight.W700
    )
    val TextLgRegular = TextStyle(
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.W400
    )
    val TextLgMedium = TextStyle(
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.W500
    )
    val TextLgBold = TextStyle(
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.W700
    )
    val TextXLRegular = TextStyle(
        fontSize = 24.sp,
        lineHeight = 28.8.sp,
        fontWeight = FontWeight.W400
    )
    val TextXLMedium = TextStyle(
        fontSize = 24.sp,
        lineHeight = 28.8.sp,
        fontWeight = FontWeight.W500
    )
    val TextXLBold = TextStyle(
        fontSize = 24.sp,
        lineHeight = 28.8.sp,
        fontWeight = FontWeight.W700
    )
    val Text2XLRegular = TextStyle(
        fontSize = 32.sp,
        lineHeight = 38.4.sp,
        fontWeight = FontWeight.W400
    )
    val Text2XLMedium = TextStyle(
        fontSize = 32.sp,
        lineHeight = 38.4.sp,
        fontWeight = FontWeight.W500
    )
    val Text2XLBold = TextStyle(
        fontSize = 32.sp,
        lineHeight = 38.4.sp,
        fontWeight = FontWeight.W700
    )
    val Text3XLRegular = TextStyle(
        fontSize = 40.sp,
        lineHeight = 44.sp,
        fontWeight = FontWeight.W400
    )
    val Text3XLMedium = TextStyle(
        fontSize = 40.sp,
        lineHeight = 44.sp,
        fontWeight = FontWeight.W500
    )
    val Text3XLBold = TextStyle(
        fontSize = 40.sp,
        lineHeight = 44.sp,
        fontWeight = FontWeight.W700
    )
}

internal val RememberTypography = Typography(
    displayLarge = TextStyles.Text3XLBold,
    displayMedium = TextStyles.Text3XLMedium,
    displaySmall = TextStyles.Text3XLRegular,
    headlineLarge = TextStyles.TextXLBold,
    headlineMedium = TextStyles.TextXLMedium,
    headlineSmall = TextStyles.TextXLRegular,
    titleLarge = TextStyles.TextBaseBold,
    titleMedium = TextStyles.TextBaseMedium,
    titleSmall = TextStyles.TextBaseRegular,
    bodyLarge = TextStyles.TextSMBold,
    bodyMedium = TextStyles.TextSMMedium,
    bodySmall = TextStyles.TextSMRegular,
    labelLarge = TextStyles.TextXSBold,
    labelMedium = TextStyles.TextXSMedium,
    labelSmall = TextStyles.TextXSRegular
)

sealed interface TextSource {

    @Composable
    operator fun plus(other: TextSource): TextSource = this + other.asString

    @Composable
    operator fun plus(other: String): TextSource = Simple("$asString$other")

    class Simple(val text: String) : TextSource
    class Resource(@StringRes val resId: Int) : TextSource

    class Formatted(val source: TextSource, vararg args: TextSource) : TextSource {

        val arguments = args
    }

    class FormattedAny(val source: TextSource, vararg args: Any) : TextSource {
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
        is TextSource.Formatted -> source.asString.format(args = arguments.map { it.asString }.toTypedArray())
        is TextSource.FormattedAny -> source.asString.format(args = arguments)
        is TextSource.StringArray -> {
            val stringArray = stringArrayResource(id = array)
            indexes.joinToString(separator = separator) { stringArray[it] }
        }

    }

val String.asTextSource get() = TextSource.Simple(this)