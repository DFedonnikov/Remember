package com.gnest.remember.core.repository

import com.gnest.remember.core.repository.db.RealmDB
import javax.inject.Inject

class MemoRepository @Inject constructor(private val realmDB: RealmDB) {

    fun getMainList() = realmDB.mainListDao.getData()

    fun closeLocal() = realmDB.close()
}