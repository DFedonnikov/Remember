package com.gnest.remember.core.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

interface Navigator {

    context(CoroutineScope)
    fun attach(navController: NavHostController, onEmptyBackStack: () -> Unit)

    fun navigateTo(screen: Screen, navOptions: NavOptions? = null)
    fun popBack()

    fun popBackTo(screen: Screen, isInclusive: Boolean = false)
}

class NavigatorImpl @Inject constructor() : Navigator {

    private val commandFlow = MutableSharedFlow<Command>(replay = 1)

    context(CoroutineScope)
            override fun attach(navController: NavHostController, onEmptyBackStack: () -> Unit) {
        launch {
            commandFlow
                .onEach { command ->
                    when (command) {
                        is Command.NavigateTo -> navController.navigate(
                            command.screen.route,
                            command.navOptions
                        )

                        Command.PopBack -> if (!navController.popBackStack()) onEmptyBackStack()
                        is Command.PopBackTo -> if (!navController.popBackStack(
                                route = command.screen.popBackRoute,
                                inclusive = command.isInclusive
                            )
                        ) onEmptyBackStack()
                    }
                }.collect()
        }

    }

    override fun navigateTo(screen: Screen, navOptions: NavOptions?) {
        commandFlow.tryEmit(Command.NavigateTo(screen, navOptions))
    }

    override fun popBack() {
        commandFlow.tryEmit(Command.PopBack)
    }

    override fun popBackTo(screen: Screen, isInclusive: Boolean) {
        commandFlow.tryEmit(Command.PopBackTo(screen, isInclusive))
    }
}

private sealed interface Command {

    data class NavigateTo(val screen: Screen, val navOptions: NavOptions?) : Command
    object PopBack : Command

    data class PopBackTo(val screen: Screen, val isInclusive: Boolean) : Command
}