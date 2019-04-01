package com.gnest.remember.presentation

import com.gnest.remember.presentation.live_data.RealmLiveData
import io.realm.RealmModel
import io.realm.RealmResults

fun <M : RealmModel> RealmResults<M>.toLiveData() = RealmLiveData(this)
