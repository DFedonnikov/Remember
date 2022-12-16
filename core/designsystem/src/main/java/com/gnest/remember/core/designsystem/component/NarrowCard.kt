package com.gnest.remember.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NarrowCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    subtitle: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .wrapContentHeight()
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.5.dp)
                .padding(start = 14.dp, end = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            title()
            subtitle()
        }
    }
}