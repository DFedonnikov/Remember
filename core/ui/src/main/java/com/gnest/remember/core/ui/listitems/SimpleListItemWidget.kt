package com.gnest.remember.core.ui.listitems

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gnest.remember.core.designsystem.component.text.AnimatedText
import com.gnest.remember.core.designsystem.icon.RememberIcons
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.RememberTheme
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.ui.noRippleClickable

@Composable
fun <T> SimpleListItemWidget(
    item: T,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    textRepresentation: (T) -> TextSource = { TextSource.Simple(it.toString()) },
    onItemClick: (T) -> Unit
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .noRippleClickable { onItemClick(item) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconAlpha by animateFloatAsState(targetValue = if (isSelected) 1.0f else 0.0f)
        Icon(
            modifier = Modifier
                .alpha(iconAlpha)
                .padding(start = 8.dp, end = 12.dp)
                .size(24.dp),
            painter = painterResource(id = RememberIcons.CheckOutlined),
            tint = AppColors.VistaBlue,
            contentDescription = "Item select icon"
        )
        AnimatedText(
            text = textRepresentation(item),
            style = if (isSelected) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SimpleListItemWidgetPreview() {
    RememberTheme {
        var isSelected by remember { mutableStateOf(false) }
        SimpleListItemWidget(
            item = "Item",
            isSelected = isSelected,
            onItemClick = { isSelected = !isSelected }
        )
    }
}