package com.gnest.remember.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gnest.remember.domain.MemoRepository
import com.gnest.remember.extensions.showNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.lang.Exception

class NotificationReceiver : BroadcastReceiver(), KoinComponent, CoroutineScope {

    override val coroutineContext by lazy { Dispatchers.IO }

    private val repository: MemoRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        intent.getIntExtra(ID_KEY, -1).takeIf { it > -1 }?.let { id ->
            launch {
                try {
                    val memo = repository.getMemo(id)
                    context.showNotification(id, memo.text)
                } catch (e: Exception) {
                }
            }
        }

    }


    companion object {

        private const val ID_KEY = "ID_KEY"

        fun build(context: Context, id: Int): Intent = Intent(context, NotificationReceiver::class.java).also {
            it.putExtra(ID_KEY, id)
        }
    }
}