package com.gnest.remember.feature.interestingidea.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import com.gnest.remember.core.navigation.extensions.push
import com.gnest.remember.feature.interestingidea.InterestingIdeaRoute
import com.gnest.remember.feature.interestingidea.list.InterestingIdeasRowList

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