package com.gnest.remember.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gnest.remember.core.designsystem.icon.RememberIcons
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.designsystem.theme.asString

@Composable
fun TopBar(
    isBackIconVisible: Boolean = true,
    isBackTitleVisible: Boolean = true,
    title: TextSource? = null,
    onBackClick: () -> Unit
) {
    Column(modifier = Modifier.statusBarsPadding()) {
        Row(
            modifier = Modifier
                .height(54.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isBackIconVisible) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = RememberIcons.ChevronLeft),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Back icon"
                    )
                }
            }
            if (isBackTitleVisible) {
                Text(
                    modifier = Modifier.clickable(onClick = onBackClick),
                    text = TextSource.Resource(R.string.back).asString,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            title?.let {
                Text(
                    modifier = Modifier.width(200.dp),
                    text = title.asString,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = AppColors.Black
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth(), color = AppColors.Black.copy(alpha = 0.1f))
    }
}