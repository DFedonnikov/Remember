package com.gnest.remember.core.designsystem.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.RememberTheme

@Composable
fun RememberSwitch(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        modifier = modifier,
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        thumbContent = {},
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = AppColors.Mint,
            uncheckedThumbColor = AppColors.FrenchGray,
            uncheckedTrackColor = AppColors.AthensGray,
            uncheckedBorderColor = Color.Transparent
        )
    )
}

@Composable
@Preview
private fun RememberSwitchPreview() {
    var isChecked by remember { mutableStateOf(false) }
    RememberTheme {
        RememberSwitch(isChecked = isChecked, onCheckedChange = { isChecked = it })
    }
}