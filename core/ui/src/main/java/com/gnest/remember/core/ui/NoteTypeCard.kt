package com.gnest.remember.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gnest.remember.core.designsystem.component.NarrowCard
import com.gnest.remember.core.designsystem.icon.Icon
import com.gnest.remember.core.designsystem.icon.RememberIcons
import com.gnest.remember.core.designsystem.icon.asPainter
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.designsystem.theme.asString

@Composable
fun NoteTypeCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    icon: Icon,
    title: TextSource,
    subtitle: TextSource,
    onClick: () -> Unit
) {
    NarrowCard(
        modifier = modifier,
        backgroundColor = backgroundColor,
        icon = {
            Image(
                painter = icon.asPainter,
                contentDescription = title.asString
            )
        },
        title = {
            Text(
                text = title.asString,
                color = AppColors.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
        },
        subtitle = {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = subtitle.asString,
                color = AppColors.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )
        },
        onClick = onClick
    )
}

@Composable
@Preview(widthDp = 400, heightDp = 120)
private fun NoteTypeCardPreview() {
    NoteTypeCard(
        modifier = Modifier.padding(16.dp),
        backgroundColor = AppColors.Jewel,
        icon = Icon.DrawableResourceIcon(RememberIcons.Idea),
        title = TextSource.Simple("Interesting Idea"),
        subtitle = TextSource.Simple("Use free text area, feel free to write it all")
    ) {}
}