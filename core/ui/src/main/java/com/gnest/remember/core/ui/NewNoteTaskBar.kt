package com.gnest.remember.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gnest.remember.core.designsystem.component.TaskBar
import com.gnest.remember.core.designsystem.component.text.Text2XSRegular
import com.gnest.remember.core.designsystem.icon.RememberIcons
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.TextSource

@Composable
fun NewNoteTaskBar(
    modifier: Modifier = Modifier,
    lastEditedDate: String,
    onSearchClicked: () -> Unit,
    onPinClicked: () -> Unit,
    onMoreClicked: () -> Unit
) {
    TaskBar(
        modifier = modifier,
        title = {
            Text2XSRegular(
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .weight(1f),
                text = TextSource.Formatted(
                    TextSource.Resource(R.string.last_edited),
                    lastEditedDate
                )
            )
        },
        firstIcon = {
            IconButton(modifier = Modifier.size(48.dp), onClick = onSearchClicked) {
                Icon(
                    painter = painterResource(id = RememberIcons.SearchSmall),
                    tint = AppColors.Ebony,
                    contentDescription = "Search notes"
                )
            }
        },
        secondIcon = {
            IconButton(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .size(48.dp),
                onClick = onPinClicked
            ) {
                Icon(
                    painter = painterResource(id = RememberIcons.Pin),
                    tint = AppColors.Ebony,
                    contentDescription = "Pin note"
                )
            }
        },
        thirdIcon = {
            IconButton(modifier = Modifier.size(48.dp), onClick = onMoreClicked) {
                Image(
                    painter = painterResource(id = RememberIcons.More),
                    contentDescription = "Notes more"
                )
            }
        }
    )
}