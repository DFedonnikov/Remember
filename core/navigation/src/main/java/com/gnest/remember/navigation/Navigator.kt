package com.gnest.remember.navigation

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
}

class NavigatorImpl @Inject constructor() : Navigator {

    private val _commandFlow = MutableSharedFlow<Command>(replay = 1)

    context(CoroutineScope)
            override fun attach(navController: NavHostController, onEmptyBackStack: () -> Unit) {
        launch {
            _commandFlow
                .onEach { command ->
                    when (command) {
                        is Command.NavigateTo -> navController.navigate(
                            command.screen.route,
                            command.navOptions
                        )

                        Command.PopBack -> if (!navController.popBackStack()) onEmptyBackStack()
                    }
                }.collect()
        }

    }

    override fun navigateTo(screen: Screen, navOptions: NavOptions?) {
        _commandFlow.tryEmit(Command.NavigateTo(screen, navOptions))
    }

    override fun popBack() {
        _commandFlow.tryEmit(Command.PopBack)
    }
}

private sealed interface Command {

    data class NavigateTo(val screen: Screen, val navOptions: NavOptions?) : Command
    object PopBack : Command
}