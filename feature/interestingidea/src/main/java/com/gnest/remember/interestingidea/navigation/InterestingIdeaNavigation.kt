package com.gnest.remember.interestingidea.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.gnest.remember.core.ui.push
import com.gnest.remember.interestingidea.InterestingIdeaRoute

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

internal val SavedStateHandle.ideaId: Long? get() = this.get<Long?>(ideaIdArg)?.takeIf { it != -1L }