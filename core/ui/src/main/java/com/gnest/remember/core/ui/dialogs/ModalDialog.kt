package com.gnest.remember.core.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gnest.remember.core.designsystem.component.dialog.RememberDialog
import com.gnest.remember.core.designsystem.component.text.TextBaseMedium
import com.gnest.remember.core.designsystem.component.text.TextBaseRegular
import com.gnest.remember.core.designsystem.component.text.TextLgBold
import com.gnest.remember.core.designsystem.icon.Icon
import com.gnest.remember.core.designsystem.icon.RememberIcons
import com.gnest.remember.core.designsystem.icon.asIcon
import com.gnest.remember.core.designsystem.icon.asPainter
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.RememberTheme
import com.gnest.remember.core.designsystem.theme.TextSource

@Composable
fun ModalDialog(
    icon: Icon,
    title: TextSource,
    text: TextSource,
    approveButtonText: TextSource,
    dismissButtonText: TextSource,
    onApprove: () -> Unit,
    onDismiss: () -> Unit,
    onDismissRequest: () -> Unit
) {
    RememberDialog(
        icon = {
            Icon(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AppColors.AeroBlue)
                    .padding(12.dp),
                painter = icon.asPainter,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "approve icon"
            )
        },
        title = { TextLgBold(text = title) },
        text = {
            TextBaseRegular(
                text = text,
                textAlign = TextAlign.Center,
                color = AppColors.Topaz
            )
        },
        buttons = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(modifier = Modifier.widthIn(min = 108.dp), onClick = onDismiss) {
                    TextBaseMedium(text = dismissButtonText)
                }
                Button(
                    modifier = Modifier
                        .widthIn(min = 108.dp)
                        .clickable(onClick = onApprove), onClick = onApprove
                ) {
                    TextBaseMedium(text = approveButtonText, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        },
        onDismissRequest = onDismissRequest
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun ModalDialogPreview() {
    RememberTheme {
        ModalDialog(
            icon = RememberIcons.CheckOutlined.asIcon,
            title = TextSource.Simple("Some title"),
            text = TextSource.Simple("Some dialog text that occupies few lines"),
            approveButtonText = TextSource.Simple("OK"),
            dismissButtonText = TextSource.Simple("Action"),
            onApprove = { },
            onDismiss = { },
            onDismissRequest = {}
        )
    }
}