package com.gnest.remember.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gnest.remember.core.designsystem.component.buttons.CloseButton
import com.gnest.remember.core.designsystem.component.text.TextBaseMedium
import com.gnest.remember.core.designsystem.icon.RememberIcons
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.RememberTheme
import com.gnest.remember.core.designsystem.theme.TextSource

@Composable
fun BottomSheetHeader(
    modifier: Modifier = Modifier,
    title: TextSource,
    isBackEnabled: Boolean = true,
    onBackClick: () -> Unit = {},
    onCloseClick: () -> Unit = {}
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier
                .size(24.dp)
                .clickable(onClick = { if (isBackEnabled) onBackClick() }),
            painter = painterResource(id = RememberIcons.ChevronLeftOutlined),
            tint = if (isBackEnabled) MaterialTheme.colorScheme.primary else AppColors.FrenchGray,
            contentDescription = "Back icon"
        )
        TextBaseMedium(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clickable(onClick = { if (isBackEnabled) onBackClick() })
                .weight(1f),
            color = if (isBackEnabled) MaterialTheme.colorScheme.primary else AppColors.FrenchGray,
            text = title
        )
        CloseButton(onClick = onCloseClick)
    }
}

@Composable
@Preview(widthDp = 360, heightDp = 64)
private fun BottomSheetHeaderPreview() {
    RememberTheme {
        BottomSheetHeader(title = TextSource.Simple("Title"))
    }
}