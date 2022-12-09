package com.gnest.remember.migration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import com.gnest.remember.R
import com.gnest.remember.core.designsystem.theme.AppColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

@AndroidEntryPoint
class RealmToRoomMigrationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = ComposeView(requireContext()).apply {
        setContent {
            DbMigrationRoute()
        }
    }

    @Composable
    @Preview
    private fun DbMigrationRoute() {
        val viewModel: RealmToRoomMigrationViewModel = hiltViewModel()
        val state = viewModel.uiState.collectAsState()
        DbMigrationScreen(
                uiState = state.value,
                onStartMigration = { viewModel.acceptIntent(MigrationIntent.StartMigration) })
    }

    @Composable
    private fun DbMigrationScreen(uiState: MigrationUiState, onStartMigration: () -> Unit) {
        MigrationSlide(isVisible = uiState is MigrationUiState.Loading) {
            LoadingScreen()
        }
        MigrationSlide(
                isVisible = uiState is MigrationUiState.Migrated,
                onContentVisible = {
                    //For more smooth UX and animation cycle
                    delay(2000)
//                    findNavController().navigate(R.id.mainList)
                },
                content = { SuccessScreen() })
        MigrationSlide(isVisible = uiState is MigrationUiState.Error) {
            ErrorScreen(onRetry = onStartMigration)
        }
    }

    @Composable
    private fun MigrationSlide(isVisible: Boolean, onContentVisible: suspend CoroutineScope.() -> Unit = {}, content: @Composable AnimatedVisibilityScope.() -> Unit) {
        val visible = remember { MutableTransitionState(!isVisible) }.apply { targetState = isVisible }
        AnimatedVisibility(visibleState = visible,
                enter = slideInHorizontally(initialOffsetX = { -it }),
                exit = slideOutHorizontally(targetOffsetX = { -it }),
                content = content)
        if (isVisible && visible.isIdle) {
            LaunchedEffect(key1 = Unit) {
                onContentVisible()
            }
        }
    }

    @Composable
    private fun LoadingScreen() {
        Column(modifier = Modifier
                .fillMaxSize()
                .background(AppColors.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
            Image(modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_migration_progress),
                    contentDescription = "Optimization Loading")
            Text(modifier = Modifier.padding(top = 48.dp), text = "Optimizing app")
            LinearProgressIndicator(modifier = Modifier
                    .padding(top = 48.dp)
                    .height(2.dp),
                    color = AppColors.SwissCoffee,
                    trackColor = AppColors.Jewel)
        }
    }

    @Composable
    private fun SuccessScreen() {
        Column(modifier = Modifier
                .fillMaxSize()
                .background(AppColors.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
            Image(modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_migration_complete),
                    contentDescription = "Optimization Success")
            Text(modifier = Modifier.padding(top = 48.dp), text = "You are good to go")
        }
    }

    @Composable
    private fun ErrorScreen(onRetry: () -> Unit) {
        Column(modifier = Modifier
                .fillMaxSize()
                .background(AppColors.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
            Image(modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_migration_error),
                    contentDescription = "Optimization Error")
            Text(modifier = Modifier.padding(top = 48.dp), text = "Something went wrong")
            Button(modifier = Modifier.padding(top = 36.dp),
                    colors = ButtonDefaults.textButtonColors(containerColor = AppColors.Jewel, contentColor = AppColors.White),
                    onClick = onRetry) {
                Text(text = "TRY AGAIN")
            }
        }
    }

    @Composable
    @Preview(widthDp = 360, heightDp = 640)
    private fun DbMigrationScreenPreview() {
        DbMigrationScreen(uiState = MigrationUiState.Loading, onStartMigration = { })
    }
}