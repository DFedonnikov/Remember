package com.gnest.remember.core.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import com.gnest.remember.core.designsystem.R

object RememberIcons {
    val HomeSolid = R.drawable.ic_home_solid
    val HomeOutlined = R.drawable.ic_home_outlined
    val FinishedSolid = R.drawable.ic_finished_solid
    val FinishedOutlined = R.drawable.ic_finished_outlined
    val SearchSolid = R.drawable.ic_search_solid
    val SearchOutlined = R.drawable.ic_search_outlined
    val SettingsSolid = R.drawable.ic_settings_solid
    val SettingsOutlined = R.drawable.ic_settings_outlined
    val AddSolid = R.drawable.ic_add
    val Idea = R.drawable.ic_idea
    val Cart = R.drawable.ic_cart
    val Goals = R.drawable.ic_goals
    val Guidance = R.drawable.ic_guidance
    val RoutineTasks = R.drawable.ic_routine_tasks
    val ChevronLeft = R.drawable.ic_chevron_left
}

sealed interface Icon {
    data class ImageVectorIcon(val imageVector: ImageVector) : Icon
    data class DrawableResourceIcon(@DrawableRes val id: Int) : Icon
}

val Icon.asPainter: Painter
    @Composable get() = when (this) {
        is Icon.ImageVectorIcon -> rememberVectorPainter(image = imageVector)
        is Icon.DrawableResourceIcon -> painterResource(id = id)
    }