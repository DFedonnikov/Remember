package com.gnest.remember.extensions

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import com.gnest.remember.R
import com.gnest.remember.presentation.ui.memolist.MemoItem
import com.gnest.remember.services.NotificationReceiver
import com.gnest.remember.ui.MainActivity

private const val ALARM_NOTIFICATION_CHANNEL = "com.gnest.remember.ALARM_NOTIFICATION_CHANNEL"

fun Context.scheduleNotificationAlarm(item: MemoItem) {
    item.alarmDate?.let { alarmDate ->
        setupAlarm { setExact(AlarmManager.RTC_WAKEUP, alarmDate.millis, getNotificationReceiver(item.id.toInt())) }
    }
}

fun Context.dismissNotificationsAlarm(ids: List<Int>) = ids.forEach { dismissNotificationAlarm(it) }

fun Context.dismissNotificationAlarm(id: Int) = setupAlarm { cancel(getNotificationReceiver(id)) }

inline fun Context.setupAlarm(setupFunc: AlarmManager.() -> Unit) = (getSystemService(Context.ALARM_SERVICE) as? AlarmManager)?.let { it.setupFunc() }

fun Context.getNotificationReceiver(id: Int): PendingIntent {
    return PendingIntent.getBroadcast(this, id, NotificationReceiver.build(this, id), PendingIntent.FLAG_UPDATE_CURRENT)
}

fun Context.showNotification(id: Int, text: String) {
    if (id < 0) return
    val notificationBuilder =
            NotificationCompat.Builder(this, ALARM_NOTIFICATION_CHANNEL).apply {
                setSmallIcon(R.drawable.ic_note)
                setContentTitle("Title")
                setContentText(text)
                priority = NotificationCompat.PRIORITY_HIGH
                setCategory(NotificationCompat.CATEGORY_ALARM)
                setContentIntent(getContentIntent(id))
            }

    val notification = notificationBuilder.build()
    notificationManager {
        buildChannel()
        notify(id, notification)
    }
}

fun Context.dismissNotification(id: Int) = notificationManager { cancel(id) }

inline fun Context.notificationManager(managerFunc: NotificationManager.() -> Unit) = (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)
        ?.let { it.managerFunc() }

private fun NotificationManager.buildChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Remember Notification Channel"
        val descriptionText = "Some description"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(ALARM_NOTIFICATION_CHANNEL, name, importance).apply {
            description = descriptionText
        }
        createNotificationChannel(channel)
    }
}

private fun Context.getContentIntent(id: Int): PendingIntent {
//    val arguments = ArchivedItemsFragmentDirections.actionArchivedListToEdit().apply {
//        memoId = id
//    }.arguments
//
//    return NavDeepLinkBuilder(this)
//            .setGraph(R.navigation.navigation_graph)
//            .setDestination(R.id.editArchivedList)
//            .setArguments(arguments)
//            .createPendingIntent()


    val intent = Intent(this, MainActivity::class.java).apply {
        putExtra(MainActivity.CONTENT_INTENT_MEMO_ID_KEY, id)
    }
    return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

fun Context.showToast(@StringRes textRes: Int, length: Int = Toast.LENGTH_LONG) = Toast.makeText(this, textRes, length).show()

fun Context.showToast(message: String, length: Int = Toast.LENGTH_LONG) = Toast.makeText(this, message, length).show()