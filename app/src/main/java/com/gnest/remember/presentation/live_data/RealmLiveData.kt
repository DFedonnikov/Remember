package com.gnest.remember.presentation.live_data

import androidx.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.RealmResults

class RealmLiveData<M : RealmModel>(private val results: RealmResults<M>) : LiveData<RealmResults<M>>() {

    private val listener = RealmChangeListener<RealmResults<M>> { value = it }

    override fun onActive() = results.addChangeListener(listener)

    override fun onInactive() = results.removeChangeListener(listener)
}