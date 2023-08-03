package com.gnest.remember.feature.newnote

import androidx.lifecycle.ViewModel
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.feature.newnote.navigation.ScreenDependency
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewNoteViewModel @Inject constructor(
    private val navigator: Navigator,
    private val screenDependency: ScreenDependency
) : ViewModel() {
    fun openInterestingIdea() {
        navigator.navigateTo(screenDependency.getInterestingIdeaScreen())
    }

    fun onBackClick() {
        navigator.popBack()
    }

}