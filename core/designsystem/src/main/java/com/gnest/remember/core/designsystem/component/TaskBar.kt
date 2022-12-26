package com.gnest.remember.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gnest.remember.core.designsystem.theme.AppColors

@Composable
fun TaskBar(
    modifier: Modifier = Modifier,
    title: @Composable RowScope.() -> Unit,
    firstIcon: @Composable RowScope.() -> Unit,
    secondIcon: @Composable RowScope.() -> Unit,
    thirdIcon: @Composable RowScope.() -> Unit
) {
    Column(modifier = modifier) {
        Divider(modifier = Modifier.fillMaxWidth(), color = AppColors.Black.copy(alpha = 0.1f))
        Row(modifier = Modifier.height(48.dp), verticalAlignment = Alignment.CenterVertically) {
            title()
            firstIcon()
            secondIcon()
            thirdIcon()
        }
    }
}