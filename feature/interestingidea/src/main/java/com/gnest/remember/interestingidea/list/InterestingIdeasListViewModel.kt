package com.gnest.remember.interestingidea.list

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.common.domain.NoteColor
import com.gnest.remember.interestingidea.domain.ObserveInterestingIdeasUseCase
import com.gnest.remember.navigation.InterestingIdeaScreen
import com.gnest.remember.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class InterestingIdeasListViewModel @Inject constructor(
    useCase: ObserveInterestingIdeasUseCase,
    private val navigator: Navigator
) :
    ViewModel() {

    val notes = useCase().map { ideas ->
        ideas.map {
            InterestingIdeaUi(
                id = it.id,
                title = TextSource.Simple(it.title),
                text = TextSource.Simple(it.text),
                color = it.color.asUiColor
            )
        }
    }

    fun openIdea(idea: InterestingIdeaUi) {
        navigator.navigateTo(InterestingIdeaScreen(idea.id))
    }

}

private val NoteColor.asUiColor: Color
    get() = when (this) {
        NoteColor.WHITE -> AppColors.White
        NoteColor.ROSE -> AppColors.Rose
        NoteColor.PICTON_BLUE -> AppColors.PictonBlue
        NoteColor.AERO_BLUE -> AppColors.AeroBlue
        NoteColor.CAPE_HONEY -> AppColors.CapeHoney
        NoteColor.WHITE_LILAC -> AppColors.WhiteLilac
        NoteColor.ATHENS_GRAY -> AppColors.AthensGray
    }