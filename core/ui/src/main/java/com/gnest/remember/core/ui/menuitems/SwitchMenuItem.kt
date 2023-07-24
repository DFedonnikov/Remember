package com.gnest.remember.core.ui.menuitems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gnest.remember.core.designsystem.component.RememberSwitch
import com.gnest.remember.core.designsystem.component.text.TextBaseMedium
import com.gnest.remember.core.designsystem.theme.RememberTheme
import com.gnest.remember.core.designsystem.theme.TextSource

@Composable
fun SwitchMenuItem(
    modifier: Modifier = Modifier,
    title: TextSource,
    isChecked: Boolean,
    onCheckChanged: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = { onCheckChanged(!isChecked) }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextBaseMedium(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f),
            text = title
        )
        RememberSwitch(
            modifier = Modifier.padding(end = 8.dp),
            isChecked = isChecked,
            onCheckedChange = onCheckChanged
        )
    }

}

@Composable
@Preview(showBackground = true, widthDp = 360)
private fun SwitchMenuItemPreview() {
    var isChecked by remember { mutableStateOf(false) }
    RememberTheme {
        SwitchMenuItem(
            title = TextSource.Simple("Reminder"),
            isChecked = isChecked
        ) {
            isChecked = it
        }
    }
}