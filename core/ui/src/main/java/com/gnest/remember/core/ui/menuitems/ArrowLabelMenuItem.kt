package com.gnest.remember.core.ui.menuitems

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gnest.remember.core.designsystem.component.text.Text2XSRegular
import com.gnest.remember.core.designsystem.component.text.TextBaseMedium
import com.gnest.remember.core.designsystem.icon.Icon
import com.gnest.remember.core.designsystem.icon.RememberIcons
import com.gnest.remember.core.designsystem.icon.asPainter
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.TextSource

@Composable
fun ArrowLabelMenuItem(
    title: TextSource,
    extra: TextSource,
    modifier: Modifier = Modifier,
    icon: Icon? = null,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = { if (isEnabled) onClick() }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val mainColor by animateColorAsState(targetValue = if (isEnabled) AppColors.Ebony else AppColors.FrenchGray)
        icon?.let {
            Icon(
                modifier = Modifier
                    .padding(start = 8.dp, end = 4.dp)
                    .size(24.dp),
                painter = it.asPainter,
                tint = mainColor,
                contentDescription = "Reminder icon"
            )
        }
        TextBaseMedium(
            modifier = Modifier
                .padding(start = 8.dp, end = 12.dp)
                .weight(1f),
            text = title,
            color = mainColor
        )
        val secondaryColor by animateColorAsState(targetValue = if (isEnabled) AppColors.Topaz else AppColors.FrenchGray)
        Text2XSRegular(text = extra, color = secondaryColor)
        Icon(
            modifier = Modifier.padding(horizontal = 8.dp),
            painter = painterResource(id = RememberIcons.ChevronRightOutlined),
            tint = secondaryColor,
            contentDescription = "Menu click"
        )
    }

}

@Composable
@Preview(showBackground = true)
private fun ArrowLabelMenuItemPreview() {
    ArrowLabelMenuItem(
        icon = Icon.DrawableResourceIcon(RememberIcons.ClockOutlined),
        title = TextSource.Simple("Set Reminder"),
        extra = TextSource.Simple("Not set")
    ) {}
}