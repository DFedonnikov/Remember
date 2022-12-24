package com.gnest.remember.feature.home

import androidx.lifecycle.ViewModel
import com.gnest.remember.feature.home.data.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(repository: HomeRepository) :
    ViewModel() {

    val hasAnyNotes = repository.hasAnyNotes()
}