package com.gnest.remember.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.gnest.remember.core.ui.AddNoteFab
import com.gnest.remember.core.designsystem.component.RememberNavigationBar
import com.gnest.remember.core.designsystem.component.RememberNavigationBarItem
import com.gnest.remember.core.designsystem.icon.Icon
import com.gnest.remember.navigation.Navigator
import com.gnest.remember.navigation.NewNoteScreen
import com.gnest.remember.navigation.RememberNavHost
import com.gnest.remember.navigation.TopLevelDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RememberApp(
    navigator: Navigator,
    appState: RememberAppState
) {
    val isBottomBarVisible = appState.isBottomBarVisible
    Scaffold(
        contentColor = MaterialTheme.colorScheme.onBackground,
        bottomBar = {
            RememberBottomBar(
                isVisible = isBottomBarVisible,
                destinations = appState.topLevelDestinations,
                onNavigateToDestination = appState::navigateToTopLevelDestination,
                currentDestination = appState.currentDestination,
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            AddNoteFab(
                isVisible = isBottomBarVisible,
                modifier = Modifier.offset(y = 60.dp)
            ) {
                navigator.navigateTo(NewNoteScreen)
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        BackHandler {
            navigator.popBack()
        }
        RememberNavHost(
            navController = appState.navController,
            modifier = Modifier
                .padding(
                    start = padding.calculateStartPadding(LayoutDirection.Ltr),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = if (isBottomBarVisible) padding.calculateBottomPadding() else 0.dp
                )
        )
    }
}

@Composable
private fun RememberBottomBar(
    isVisible: Boolean,
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
    ) {
        RememberNavigationBar {
            destinations.forEach { destination ->
                val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
                RememberNavigationBarItem(
                    selected = selected,
                    onClick = { onNavigateToDestination(destination) },
                    icon = {
                        val icon = if (selected) {
                            destination.selectedIcon
                        } else {
                            destination.unselectedIcon
                        }
                        when (icon) {
                            is Icon.ImageVectorIcon -> Icon(
                                imageVector = icon.imageVector,
                                contentDescription = null
                            )

                            is Icon.DrawableResourceIcon -> Icon(
                                painter = painterResource(id = icon.id),
                                contentDescription = null
                            )
                        }
                    },
                    label = { Text(stringResource(destination.iconTextId)) }
                )
            }
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false