package com.gnest.remember.feature.interestingidea.list

import androidx.lifecycle.ViewModel
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.feature.interestingidea.domain.ObserveInterestingIdeasUseCase
import com.gnest.remember.core.navigation.InterestingIdeaScreen
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.core.noteuimapper.asUiColor
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
                color = it.color.asUiColor()
            )
        }
    }

    fun openIdea(idea: InterestingIdeaUi) {
        navigator.navigateTo(InterestingIdeaScreen(idea.id))
    }

}