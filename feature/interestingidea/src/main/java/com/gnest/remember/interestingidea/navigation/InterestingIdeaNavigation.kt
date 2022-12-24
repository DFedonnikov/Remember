package com.gnest.remember.interestingidea.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.gnest.remember.core.ui.push
import com.gnest.remember.interestingidea.InterestingIdeaRoute
import com.gnest.remember.interestingidea.list.InterestingIdeasRowList

const val interestingIdeaRoute = "interestingIdeaRoute"
internal const val ideaIdArg = "InterestingIdeaId"

fun NavController.navigateToInterestingIdea(
    id: Long = -1L,
    navOptions: NavOptions? = null
) {
    this.navigate("$interestingIdeaRoute/$id", navOptions)
}

fun NavGraphBuilder.interestingIdeaScreen(onBackClick: () -> Unit) {
    push(
        route = "$interestingIdeaRoute/{$ideaIdArg}",
        arguments = listOf(navArgument(ideaIdArg) { type = NavType.LongType })
    ) {
        InterestingIdeaRoute(onBackClick = onBackClick)
    }
}


fun interestingIdeasRowList(): @Composable () -> Unit = {
    InterestingIdeasRowList()
}

internal val SavedStateHandle.ideaId: Long? get() = this.get<Long?>(ideaIdArg)?.takeIf { it != -1L }