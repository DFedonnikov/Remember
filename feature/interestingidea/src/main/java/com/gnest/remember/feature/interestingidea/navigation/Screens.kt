package com.gnest.remember.feature.interestingidea.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.gnest.remember.core.navigation.Screen

internal class InterestingIdeaScreen(private val id: Long? = null) : Screen {

    override val route get() = "$interestingIdeaRoute/$id"

    companion object {

        private const val interestingIdeaRoute = "interestingIdeaRoute"
        const val ideaIdArg = "InterestingIdeaId"
        const val routePattern = "$interestingIdeaRoute/{$ideaIdArg}"
        val args = listOf(navArgument(ideaIdArg) { type = NavType.LongType })

    }
}