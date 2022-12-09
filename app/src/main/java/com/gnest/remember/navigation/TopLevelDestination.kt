package com.gnest.remember.navigation

import com.gnest.remember.core.designsystem.icon.Icon
import com.gnest.remember.core.designsystem.icon.RememberIcons
import com.gnest.remember.feature.home.R.string as homeR
import com.gnest.remember.feature.finished.R.string as finishedR
import com.gnest.remember.feature.search.R.string as searchR
import com.gnest.remember.feature.settings.R.string as settingsR

enum class TopLevelDestination(
        val selectedIcon: Icon,
        val unselectedIcon: Icon,
        val iconTextId: Int
) {
    HOME(
            selectedIcon = Icon.DrawableResourceIcon(RememberIcons.HomeSolid),
            unselectedIcon = Icon.DrawableResourceIcon(RememberIcons.HomeOutlined),
            iconTextId = homeR.home
    ),
    FINISHED(
            selectedIcon = Icon.DrawableResourceIcon(RememberIcons.FinishedSolid),
            unselectedIcon = Icon.DrawableResourceIcon(RememberIcons.FinishedOutlined),
            iconTextId = finishedR.finished
    ),
    SEARCH(
            selectedIcon = Icon.DrawableResourceIcon(RememberIcons.SearchSolid),
            unselectedIcon = Icon.DrawableResourceIcon(RememberIcons.SearchOutlined),
            iconTextId = searchR.search
    ),
    SETTINGS(
            selectedIcon = Icon.DrawableResourceIcon(RememberIcons.SettingsSolid),
            unselectedIcon = Icon.DrawableResourceIcon(RememberIcons.SettingsOutlined),
            iconTextId = settingsR.settings
    )
}