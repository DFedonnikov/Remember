package com.gnest.remember.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelsFactory @Inject constructor(
        private val memoListViewModel: Lazy<MemoListViewModel>) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass is MemoListViewModel) {

        }

        return when (modelClass) {
            is MemoListViewModel -> memoListViewModel.value
            else -> throw IllegalArgumentException("Unknown view model class $modelClass")
        }
    }
}