package com.gnest.remember.core.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector
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
}

sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector) : Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int) : Icon()
}
