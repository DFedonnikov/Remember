package com.gnest.remember.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.gnest.remember.core.designsystem.icon.RememberIcons

@Composable
fun AddNoteFab(
    isVisible: Boolean,
    modifier: Modifier,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
    ) {
        FloatingActionButton(
            shape = CircleShape,
            containerColor = Color.Transparent,
            elevation = elevation,
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.bg_fab),
                    contentDescription = ""
                )
                Image(
                    modifier = Modifier.shadow(elevation = 4.dp, CircleShape, clip = false),
                    imageVector = ImageVector.vectorResource(id = RememberIcons.AddSolid),
                    contentDescription = "Fab add"
                )
            }
        }
    }
}