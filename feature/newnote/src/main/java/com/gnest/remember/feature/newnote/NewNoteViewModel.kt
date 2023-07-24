package com.gnest.remember.feature.newnote

import androidx.lifecycle.ViewModel
import com.gnest.remember.core.navigation.InterestingIdeaScreen
import com.gnest.remember.core.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewNoteViewModel @Inject constructor(private val navigator: Navigator) : ViewModel() {
    fun openInterestingIdea() {
        navigator.navigateTo(InterestingIdeaScreen())
    }

    fun onBackClick() {
        navigator.popBack()
    }

}