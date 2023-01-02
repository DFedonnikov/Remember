package com.gnest.remember.interestingidea.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import com.gnest.remember.navigation.extensions.push
import com.gnest.remember.interestingidea.InterestingIdeaRoute
import com.gnest.remember.interestingidea.list.InterestingIdeasRowList
import com.gnest.remember.navigation.InterestingIdeaScreen

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.interestingIdeaScreen() {
    push(
        route = InterestingIdeaScreen.routePattern,
        arguments = InterestingIdeaScreen.args,
    ) {
        InterestingIdeaRoute()
    }
}


fun interestingIdeasRowList(): @Composable () -> Unit = {
    InterestingIdeasRowList()
}

internal var SavedStateHandle.ideaId: Long?
    get() = get<Long?>(InterestingIdeaScreen.ideaIdArg)?.takeIf { it != -1L }
    set(value) {
        this[InterestingIdeaScreen.ideaIdArg] = value
    }