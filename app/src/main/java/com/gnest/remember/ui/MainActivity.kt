package com.gnest.remember.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.gnest.remember.core.designsystem.theme.RememberTheme
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.core.screensprovider.AppModuleScreenDependency
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(ExperimentalMaterialNavigationApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    internal lateinit var navigator: Navigator

    @Inject
    internal lateinit var screensDependency: AppModuleScreenDependency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val systemUiController = rememberSystemUiController()
            //TODO update when dark theme is implemented
//            val useDarkIcons = isSystemInDarkTheme().not()
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = true)
            }
            RememberTheme {
                val appState = rememberAppState(screenDependency = screensDependency, navigator = navigator)
                LaunchedEffect(key1 = appState.navController) {
                    navigator.attach(appState.navController, onEmptyBackStack = { finish() })
                }
                RememberApp(
                    navigator = navigator,
                    appState = appState,
                    startDestination = screensDependency.getHomeScreen(),
                    newNoteScreen = screensDependency.getNewNoteScreen()
                )
            }
        }
    }
}