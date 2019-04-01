package com.gnest.remember.core.repository.db

import javax.inject.Inject

class RealmDB @Inject constructor(
        val mainListDao: MainListDao) {


    fun close() {
        mainListDao.close()
    }
}