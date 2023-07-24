package com.gnest.remember.core.ui.listitems

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gnest.remember.core.designsystem.component.text.TextBaseMedium
import com.gnest.remember.core.designsystem.theme.RememberTheme
import com.gnest.remember.core.designsystem.theme.TextSource

@Composable
fun CheckboxListItemWidget(
    modifier: Modifier = Modifier,
    title: TextSource,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(modifier = modifier) {
        Checkbox(
            modifier = Modifier.padding(start = 8.dp),
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        TextBaseMedium(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .padding(vertical = 12.dp),
            text = title
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CheckboxListItemWidgetPreview() {
    RememberTheme {
        var isChecked by remember { mutableStateOf(false) }
        CheckboxListItemWidget(title = TextSource.Simple("Label"), isChecked = isChecked) { isChecked = it }
    }
}