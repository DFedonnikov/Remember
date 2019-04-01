package com.gnest.remember.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gnest.remember.core.repository.MemoRepository
import com.gnest.remember.model.db.data.Memo
import com.gnest.remember.presentation.toLiveData
import io.realm.RealmResults
import javax.inject.Inject

class MemoListViewModel @Inject constructor(private val memoRepository: MemoRepository) : ViewModel() {

    var list: LiveData<RealmResults<Memo>> = memoRepository.getMainList()
            .toLiveData()


    override fun onCleared() {
        memoRepository.closeLocal()
        super.onCleared()
    }
}